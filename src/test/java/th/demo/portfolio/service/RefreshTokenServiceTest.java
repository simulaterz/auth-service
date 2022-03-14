package th.demo.portfolio.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import th.demo.portfolio.component.JWTComponent;
import th.demo.portfolio.component.SHAComponent;
import th.demo.portfolio.configuration.property.JwtProperty;
import th.demo.portfolio.exception.UnauthorizedException;
import th.demo.portfolio.model.BaseUserModel;
import th.demo.portfolio.model.inbound.request.RefreshTokenRequest;
import th.demo.portfolio.model.redis.RefreshTokenRedis;
import th.demo.portfolio.repository.AuthenticationRedisRepository;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("Test -> RefreshTokenService")
class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService service;

    @Mock
    private JWTComponent jwtComponent;

    @Mock
    private SHAComponent shaComponent;

    @Mock
    private AuthenticationRedisRepository authRedisRepository;

    @Spy
    private JwtProperty property = new JwtProperty();

    private final long FIVE_HOURS = 5 * 60 * 60 * 1000;
    private final long SIX_HOURS = 6 * 60 * 60 * 1000;

    private final String USER = "my-user";
    private final String OLD_REFRESH_TOKEN = "OLD-REFRESH-TOKEN";
    private final String HASH_OLD_ACCESS_TOKEN = "HASH-OLD-ACCESS-TOKEN";
    private final String HASH_OLD_REFRESH_TOKEN = "HASH-OLD-REFRESH-TOKEN";

    private RefreshTokenRequest request;

    @BeforeEach
    public void setup() throws NoSuchAlgorithmException {
        var expire = JwtProperty.JwtExpireProperty.builder()
                .access(FIVE_HOURS)
                .refresh(SIX_HOURS)
                .build();

        property.setSecret("jwtsecret");
        property.setExpire(expire);

        request = RefreshTokenRequest.builder().refreshToken(OLD_REFRESH_TOKEN).build();

        doReturn(HASH_OLD_REFRESH_TOKEN ).when(shaComponent).toSHA256String(OLD_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("refreshToken, expected success with new token")
    void refreshToken() {
        var refreshTokenRedis = RefreshTokenRedis.builder().accessTokenHash(HASH_OLD_ACCESS_TOKEN).build();

        doReturn(refreshTokenRedis).when(authRedisRepository).getRefreshTokenDetail(HASH_OLD_REFRESH_TOKEN);
        doNothing().when(authRedisRepository).deleteOldToken(HASH_OLD_ACCESS_TOKEN, HASH_OLD_REFRESH_TOKEN);

        doReturn(USER).when(jwtComponent).getClaimFromToken(eq(OLD_REFRESH_TOKEN), any());
        doReturn("ACCESS-TOKEN").when(jwtComponent).generateToken(USER, FIVE_HOURS);
        doReturn("REFRESH-TOKEN").when(jwtComponent).generateToken(USER, SIX_HOURS);

        var response = service.refreshToken(request);

        verify(authRedisRepository, times(1)).saveAccessTokenHashToRedis(anyString(), any(BaseUserModel.class), anyLong());
        verify(authRedisRepository, times(1)).saveRefreshTokenHashToRedis(anyString(), anyString(), any(BaseUserModel.class), anyLong());

        assertEquals("ACCESS-TOKEN", response.getAccessToken());
        assertEquals("REFRESH-TOKEN", response.getRefreshToken());
    }

    @Test
    @DisplayName("refreshToken, expected not found and Unauthorized")
    void refreshTokenNotFound() {
        doReturn(null).when(authRedisRepository).getRefreshTokenDetail(HASH_OLD_REFRESH_TOKEN);

        var exception = assertThrows(UnauthorizedException.class, () -> {
            service.refreshToken(request);
        });

        assertTrue(exception.getMessage().contains("Token not found."));
    }
}