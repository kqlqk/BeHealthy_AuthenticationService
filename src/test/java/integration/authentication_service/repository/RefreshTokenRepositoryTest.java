package integration.authentication_service.repository;

import annotations.RepositoryTest;
import me.kqlqk.behealthy.authentication_service.model.RefreshToken;
import me.kqlqk.behealthy.authentication_service.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
public class RefreshTokenRepositoryTest {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    public void findById_shouldFindByIdOrReturnNull() {
        RefreshToken refreshToken = refreshTokenRepository.findById(1);

        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken.getId()).isEqualTo(1);

        RefreshToken nullRefreshToken = refreshTokenRepository.findById(99);
        assertThat(nullRefreshToken).isNull();
    }

    @Test
    public void findById_shouldFindByTokenOrReturnNull() {
        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQG1haWwuY29tIiwiaWF0IjoxNjYwNDkzMzEzLCJleHAiOjk2NjMwODUzMTN9.cLIXPLbOyC-4Rt1pOTmQQ7s0gYV0u0AlwYVmc0W3TZE");

        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken.getId()).isEqualTo(1);

        RefreshToken nullRefreshToken = refreshTokenRepository.findByToken("badToken");
        assertThat(nullRefreshToken).isNull();
    }
}
