package com.travelplanner.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String notificationType;
    private String title;
    private String message;
    private boolean isRead;
    private boolean isSent;
    private String sentVia;
    private String priority;
    private String relatedEntityType;
    private Long relatedEntityId;
    private String metadata;
    private LocalDateTime scheduledFor;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    private UserResponse user;

    // Constructor from Notification entity
    public NotificationResponse(com.travelplanner.backend.entity.Notification notification) {
        this.id = notification.getId();
        this.notificationType = notification.getNotificationType();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.isRead = notification.isRead();
        this.isSent = notification.isSent();
        this.sentVia = notification.getSentVia();
        this.priority = notification.getPriority();
        this.relatedEntityType = notification.getRelatedEntityType();
        this.relatedEntityId = notification.getRelatedEntityId();
        this.metadata = notification.getMetadata();
        this.scheduledFor = notification.getScheduledFor();
        this.sentAt = notification.getSentAt();
        this.readAt = notification.getReadAt();
        this.createdAt = notification.getCreatedAt();
        this.user = new UserResponse(notification.getUser());
    }
}
