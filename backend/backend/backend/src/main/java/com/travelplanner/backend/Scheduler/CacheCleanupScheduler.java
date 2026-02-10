package com.travelplanner.backend.Scheduler;
import com.travelplanner.backend.service.OfflineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CacheCleanupScheduler.class);

    @Autowired
    private OfflineService offlineService;

    // Run daily at 3 AM to clean up expired cache
    @Scheduled(cron = "0 0 3 * * ?") // 3:00 AM every day
    public void cleanupExpiredCache() {
        try {
            logger.info("Running scheduled task: cleanupExpiredCache");
            offlineService.cleanupExpiredCache();
            logger.info("Completed scheduled task: cleanupExpiredCache");
        } catch (Exception e) {
            logger.error("Error in cleanupExpiredCache scheduled task", e);
        }
    }

    // Run weekly to clean up old exports
    @Scheduled(cron = "0 0 4 * * 0") // 4:00 AM every Sunday
    public void cleanupOldExports() {
        try {
            logger.info("Running scheduled task: cleanupOldExports");
            // Additional cleanup logic could be added here
            logger.info("Completed scheduled task: cleanupOldExports");
        } catch (Exception e) {
            logger.error("Error in cleanupOldExports scheduled task", e);
        }
    }
}
