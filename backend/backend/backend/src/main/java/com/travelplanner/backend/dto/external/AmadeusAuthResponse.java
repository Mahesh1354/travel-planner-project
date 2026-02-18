package com.travelplanner.backend.dto.external;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class AmadeusAuthResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("state")
    private String state;
}