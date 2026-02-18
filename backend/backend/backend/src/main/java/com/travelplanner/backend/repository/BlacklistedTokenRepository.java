package com.travelplanner.backend.repository;

import com.travelplanner.backend.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    boolean existsByTokenHash(String tokenHash);

    // Keep this for backward compatibility if needed
    boolean existsByToken(String token);

    @Query("SELECT b.id FROM BlacklistedToken b WHERE b.expiresAt <= :now ORDER BY b.id")
    List<Long> findExpiredTokenIds(@Param("now") LocalDateTime now,
                                   @Param("limit") int limit);

    @Modifying
    @Query("DELETE FROM BlacklistedToken b WHERE b.id IN :ids")
    int deleteAllByIdIn(@Param("ids") List<Long> ids);

    // Keep original delete method for backward compatibility
    void deleteByExpiresAtBefore(LocalDateTime now);
}