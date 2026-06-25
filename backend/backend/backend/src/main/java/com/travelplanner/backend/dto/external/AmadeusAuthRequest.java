package com.travelplanner.backend.dto.external;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AmadeusAuthRequest {
    private String grantType;
    private String clientId;
    private String clientSecret;
}