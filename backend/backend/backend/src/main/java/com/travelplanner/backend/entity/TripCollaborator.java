package com.travelplanner.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_collaborators",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"trip_id", "user_id"})
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripCollaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "permission_level")
    @Enumerated(EnumType.STRING)
    private PermissionLevel permissionLevel = PermissionLevel.VIEW;

    @Column(name = "is_accepted")
    private boolean isAccepted = false;

    @Column(name = "invited_at")
    private LocalDateTime invitedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Pre-persist method
    @PrePersist
    protected void onCreate() {
        invitedAt = LocalDateTime.now();
    }

    // Permission level enum
    public enum PermissionLevel {
        VIEW,
        EDIT,
        ADMIN
    }
}
