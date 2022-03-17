package th.demo.portfolio.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import th.demo.portfolio.component.SHAComponent;
import th.demo.portfolio.exception.CustomException;
import th.demo.portfolio.model.inbound.request.SignOutRequest;
import th.demo.portfolio.repository.AuthenticationRedisRepository;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("Test -> SignOutService")
class SignOutServiceTest {

    @InjectMocks
    private SignOutService service;

    @Mock
    private SHAComponent shaComponent;

    @Mock
    private AuthenticationRedisRepository authRedisRepository;

    @Test
    @SneakyThrows
    @DisplayName("signOut, expected success")
    void signOut() {
        var accessToken = "accessToken";
        var refreshToken = "refreshToken";
        var hashAccessToken = "hashAccessToken";
        var hashRefreshToken = "hashRefreshToken";

        doReturn(hashAccessToken).when(shaComponent).toSHA256String(accessToken);
        doReturn(hashRefreshToken).when(shaComponent).toSHA256String(refreshToken);
        doNothing().when(authRedisRepository).deleteOldToken(hashAccessToken, hashRefreshToken);

        assertDoesNotThrow(() -> service.signOut(
                SignOutRequest.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build()
                )
        );

        verify(shaComponent, times(1)).toSHA256String(accessToken);
        verify(shaComponent, times(1)).toSHA256String(refreshToken);
        verify(authRedisRepository, times(1)).deleteOldToken(hashAccessToken, hashRefreshToken);
    }

    @Test
    @SneakyThrows
    @DisplayName("signOut, expected failed.")
    void signOutFailed() {
        var accessToken = "accessToken";
        var refreshToken = "refreshToken";
        var hashAccessToken = "hashAccessToken";

        doReturn(hashAccessToken).when(shaComponent).toSHA256String(accessToken);
        doThrow(new NoSuchAlgorithmException()).when(shaComponent).toSHA256String(refreshToken);

        var exception = assertThrows(CustomException.class, () -> service.signOut(
                        SignOutRequest.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build()
                )
        );

        verify(shaComponent, times(1)).toSHA256String(accessToken);
        verify(shaComponent, times(1)).toSHA256String(refreshToken);

        assertTrue(exception.getMessage().contains("Exception during signOut."));
    }
}