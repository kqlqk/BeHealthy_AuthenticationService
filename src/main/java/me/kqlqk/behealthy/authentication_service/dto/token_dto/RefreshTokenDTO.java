package me.kqlqk.behealthy.authentication_service.dto.token_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDTO {
    private final String type = "Bearer";

    private String refreshToken;
}
