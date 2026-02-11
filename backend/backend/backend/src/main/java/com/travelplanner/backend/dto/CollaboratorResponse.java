package com.travelplanner.backend.dto;

import com.travelplanner.backend.entity.TripCollaborator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollaboratorResponse {

    private Long id;
    private String permissionLevel;
    private boolean accepted;
    private LocalDateTime invitedAt;
    private LocalDateTime acceptedAt;
    private UserResponse user;

    /**
     * Maps TripCollaborator entity to CollaboratorResponse DTO
     */
    public CollaboratorResponse(TripCollaborator collaborator) {
        if (collaborator == null) return;

        this.id = collaborator.getId();
        this.permissionLevel = collaborator.getPermissionLevel() != null
                ? collaborator.getPermissionLevel().name()
                : null;
        this.accepted = collaborator.isAccepted();
        this.invitedAt = collaborator.getInvitedAt();
        this.acceptedAt = collaborator.getAcceptedAt();
        this.user = collaborator.getUser() != null
                ? new UserResponse(collaborator.getUser())
                : null;
    }
}
