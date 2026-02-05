package com.travelplanner.backend.dto;
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
    private boolean isAccepted;
    private LocalDateTime invitedAt;
    private LocalDateTime acceptedAt;
    private UserResponse user;

    // Constructor from TripCollaborator entity
    public CollaboratorResponse(com.travelplanner.backend.entity.TripCollaborator collaborator) {
        this.id = collaborator.getId();
        this.permissionLevel = collaborator.getPermissionLevel().name();
        this.isAccepted = collaborator.isAccepted();
        this.invitedAt = collaborator.getInvitedAt();
        this.acceptedAt = collaborator.getAcceptedAt();
        this.user = new UserResponse(collaborator.getUser());
    }
}