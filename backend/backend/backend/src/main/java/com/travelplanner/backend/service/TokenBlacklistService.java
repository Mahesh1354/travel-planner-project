package com.travelplanner.backend.service;

import com.travelplanner.backend.entity.BlacklistedToken;
import com.travelplanner.backend.repository.BlacklistedTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class TokenBlacklistService {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);

    @Value("${app.token.blacklist.default-expiry-hours:24}")
    private int defaultExpiryHours;

    @Value("${app.token.blacklist.cleanup-batch-size:1000}")
    private int cleanupBatchSize;

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    /**
     * Blacklist a token with specific expiration time
     */
    @Transactional
    public void blacklistToken(String token, String reason, LocalDateTime expiresAt) {
        // Validate input
        if (!StringUtils.hasText(token)) {
            logger.warn("Attempted to blacklist null or empty token");
            return;
        }

        if (expiresAt == null) {
            logger.warn("Expiration time is null, using default expiry");
            expiresAt = LocalDateTime.now().plusHours(defaultExpiryHours);
        }

        // Hash the token before storing (security best practice)
        String tokenHash = hashToken(token);

        // Check if already blacklisted (using hash)
        if (isTokenHashBlacklisted(tokenHash)) {
            logger.info("Token already blacklisted, skipping: hash={}",
                    truncateHash(tokenHash));
            return;
        }

        try {
            BlacklistedToken blacklistedToken = new BlacklistedToken();
            blacklistedToken.setTokenHash(tokenHash);
            // Store only the hash, not the actual token
            blacklistedToken.setToken("HASHED"); // Set a placeholder or remove this field from entity
            blacklistedToken.setBlacklistedAt(LocalDateTime.now());
            blacklistedToken.setExpiresAt(expiresAt);
            blacklistedToken.setReason(reason);

            blacklistedTokenRepository.save(blacklistedToken);

            logger.info("Token blacklisted successfully. Hash: {}, Reason: {}, Expires: {}",
                    truncateHash(tokenHash), reason, expiresAt);

        } catch (Exception e) {
            logger.error("Failed to blacklist token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to blacklist token", e);
        }
    }

    /**
     * Blacklist a token with default expiration (24 hours)
     */
    @Transactional
    public void blacklistToken(String token, String reason) {
        LocalDateTime defaultExpiresAt = LocalDateTime.now().plusHours(defaultExpiryHours);
        blacklistToken(token, reason, defaultExpiresAt);
    }

    /**
     * Check if a token is blacklisted (using token hash)
     */
    @Transactional(readOnly = true)
    public boolean isTokenBlacklisted(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        String tokenHash = hashToken(token);
        return isTokenHashBlacklisted(tokenHash);
    }

    /**
     * Internal method to check if a token hash is blacklisted
     */
    @Transactional(readOnly = true)
    protected boolean isTokenHashBlacklisted(String tokenHash) {
        if (tokenHash == null) {
            return false;
        }
        return blacklistedTokenRepository.existsByTokenHash(tokenHash);
    }

    /**
     * Clean up expired tokens in batches to prevent memory issues
     * Runs daily at 3 AM
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        logger.info("Starting expired token cleanup");
        LocalDateTime now = LocalDateTime.now();
        int totalDeleted = 0;

        try {
            // Delete in batches to avoid long-running transactions
            List<Long> expiredTokenIds;
            do {
                expiredTokenIds = blacklistedTokenRepository.findExpiredTokenIds(now, cleanupBatchSize);
                if (!expiredTokenIds.isEmpty()) {
                    int deletedCount = blacklistedTokenRepository.deleteAllByIdIn(expiredTokenIds);
                    totalDeleted += deletedCount;
                    logger.debug("Cleaned up batch of {} expired tokens", deletedCount);
                }
            } while (expiredTokenIds.size() == cleanupBatchSize);

            logger.info("Expired token cleanup completed. Total tokens removed: {}", totalDeleted);

        } catch (Exception e) {
            logger.error("Error during token cleanup: {}", e.getMessage(), e);
        }
    }

    /**
     * Hash a token using SHA-256 for secure storage
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-256 algorithm not available", e);
            throw new RuntimeException("Unable to hash token", e);
        }
    }

    /**
     * Truncate hash for logging (first 8 chars + "...")
     */
    private String truncateHash(String hash) {
        if (hash == null || hash.length() <= 10) {
            return hash;
        }
        return hash.substring(0, 8) + "...";
    }
}