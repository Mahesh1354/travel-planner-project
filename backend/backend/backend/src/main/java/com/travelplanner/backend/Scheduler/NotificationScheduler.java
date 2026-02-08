package com.travelplanner.backend.Scheduler;

import com.travelplanner.backend.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);

    @Autowired
    private NotificationService notificationService;

    // Run every 5 minutes to send pending notifications
    @Scheduled(fixedRate = 300000) // 300,000 ms = 5 minutes
    public void sendPendingNotifications() {
        try {
            logger.info("Running scheduled task: sendPendingNotifications");
            notificationService.sendPendingNotifications();
            logger.info("Completed scheduled task: sendPendingNotifications");
        } catch (Exception e) {
            logger.error("Error in sendPendingNotifications scheduled task", e);
        }
    }

    // Run every hour to check for flight updates
    @Scheduled(fixedRate = 3600000) // 3,600,000 ms = 1 hour
    public void checkFlightUpdates() {
        try {
            logger.info("Running scheduled task: checkFlightUpdates");
            notificationService.generateFlightUpdateNotifications();
            logger.info("Completed scheduled task: checkFlightUpdates");
        } catch (Exception e) {
            logger.error("Error in checkFlightUpdates scheduled task", e);
        }
    }

    // Run every 6 hours to check for weather alerts
    @Scheduled(fixedRate = 21600000) // 21,600,000 ms = 6 hours
    public void checkWeatherAlerts() {
        try {
            logger.info("Running scheduled task: checkWeatherAlerts");
            notificationService.generateWeatherAlertNotifications();
            logger.info("Completed scheduled task: checkWeatherAlerts");
        } catch (Exception e) {
            logger.error("Error in checkWeatherAlerts scheduled task", e);
        }
    }

    // Run every 30 minutes to check for group activity updates
    @Scheduled(fixedRate = 1800000) // 1,800,000 ms = 30 minutes
    public void checkGroupActivities() {
        try {
            logger.info("Running scheduled task: checkGroupActivities");
            notificationService.generateGroupActivityNotifications();
            logger.info("Completed scheduled task: checkGroupActivities");
        } catch (Exception e) {
            logger.error("Error in checkGroupActivities scheduled task", e);
        }
    }

    // Run daily at 8 AM to send daily digests
    @Scheduled(cron = "0 0 8 * * ?") // 8:00 AM every day
    public void sendDailyDigest() {
        try {
            logger.info("Running scheduled task: sendDailyDigest");
            // This could be implemented to send daily summaries
            // For now, we'll just log it
            logger.info("Daily digest would be sent here");
            logger.info("Completed scheduled task: sendDailyDigest");
        } catch (Exception e) {
            logger.error("Error in sendDailyDigest scheduled task", e);
        }
    }
}