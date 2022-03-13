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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import th.demo.portfolio.component.JWTComponent;
import th.demo.portfolio.configuration.property.JwtProperty;
import th.demo.portfolio.configuration.property.UsernamePasswordProperty;
import th.demo.portfolio.exception.UnauthorizedException;
import th.demo.portfolio.model.inbound.request.SignInRequest;
import th.demo.portfolio.model.inbound.response.SignInResponse;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Test -> JWTComponent")
class SignInServiceTest {

    @InjectMocks
    private SignInService service;

    @Mock
    private JWTComponent jwtComponent;

    @Spy
    private JwtProperty property = new JwtProperty();

    @Spy
    private UsernamePasswordProperty usernamePasswordProperty = new UsernamePasswordProperty();

    private final long FIVE_HOURS = 5 * 60 * 60 * 1000;
    private final long SIX_HOURS = 6 * 60 * 60 * 1000;
    private final String USER = "my-user";
    private final String PASSWORD = "pwd";

    private SignInRequest request;

    @BeforeEach
    public void setup() {
        var expire = JwtProperty.JwtExpireProperty.builder()
                .access(FIVE_HOURS)
                .refresh(SIX_HOURS)
                .build();

        property.setSecret("jwtsecret");
        property.setExpire(expire);

        usernamePasswordProperty.setList(Map.of(USER, PASSWORD));

        when(jwtComponent.generateToken(USER, FIVE_HOURS)).thenReturn("ACCESS-TOKEN");
        when(jwtComponent.generateToken(USER, SIX_HOURS)).thenReturn("REFRESH-TOKEN");

        request = SignInRequest.builder()
                .username(USER)
                .password(PASSWORD)
                .build();
    }

    @Test
    @DisplayName("signIn, expected success")
    void signIn() {
        var response = service.signIn(request);
        var expectedResponse = SignInResponse.builder()
                .accessToken("ACCESS-TOKEN")
                .refreshToken("REFRESH-TOKEN")
                .build();

        assertEquals(expectedResponse, response);

        verify(jwtComponent, times(1)).generateToken(USER, FIVE_HOURS);
        verify(jwtComponent, times(1)).generateToken(USER, SIX_HOURS);
    }

    @Test
    @DisplayName("signIn invalid credential, expected failed with UnauthorizedException")
    void signInWithWrongPassword() {
        // set invalid password
        usernamePasswordProperty.setList(Map.of(USER, "INVALID-PASSWORD"));

        var exception = assertThrows(UnauthorizedException.class, () -> {
            service.signIn(request);
        });

        assertTrue(exception.getMessage().contains("Invalid username or password"));
    }
}