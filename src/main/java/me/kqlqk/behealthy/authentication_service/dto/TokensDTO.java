package me.kqlqk.behealthy.authentication_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokensDTO {
    private final String type = "Bearer";

    private String accessToken;
    private String refreshToken;
}
