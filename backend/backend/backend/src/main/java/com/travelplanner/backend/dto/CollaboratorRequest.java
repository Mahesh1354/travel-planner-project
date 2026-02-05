package com.travelplanner.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollaboratorRequest {

    private String userEmail;

    private String permissionLevel = "VIEW"; // VIEW, EDIT, ADMIN
}