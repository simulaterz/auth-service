package th.demo.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import th.demo.portfolio.exception.RestExceptionResolver;
import th.demo.portfolio.exception.UnauthorizedException;
import th.demo.portfolio.model.inbound.request.RefreshTokenRequest;
import th.demo.portfolio.model.inbound.request.SignInRequest;
import th.demo.portfolio.model.inbound.response.RefreshTokenResponse;
import th.demo.portfolio.model.inbound.response.SignInResponse;
import th.demo.portfolio.service.RefreshTokenService;
import th.demo.portfolio.service.SignInService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("Test -> AuthController")
class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController controller;

    @Mock
    private SignInService signInService;

    @Mock
    private RefreshTokenService refreshTokenService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new RestExceptionResolver(), controller)
                .build();
    }

    @Test
    @DisplayName("signIn, expected success")
    void signIn() throws Exception {
        var response = SignInResponse.builder()
                .accessToken("ACCESS-TOKEN")
                .refreshToken("REFRESH-TOKEN")
                .build();

        doReturn(response)
                .when(signInService)
                .signIn(any());

        var request = SignInRequest.builder()
                .username("USER")
                .password("PASSWORD")
                .build();

        mockMvc.perform(post("/api/v1/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.accessToken").value("ACCESS-TOKEN"))
                .andExpect(jsonPath("$.refreshToken").value("REFRESH-TOKEN"))
                .andDo(print());
    }

    @Test
    @DisplayName("signIn, expected Unauthorized")
    void signInUnauthorized() throws Exception {
        doThrow(new UnauthorizedException("Error"))
                .when(signInService)
                .signIn(any());

        var request = SignInRequest.builder()
                .username("USER")
                .password("PASSWORD")
                .build();

        mockMvc.perform(post("/api/v1/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("signIn, expected BadRequest")
    void signInBadRequest() throws Exception {
        var request = SignInRequest.builder()
                .username("USER")
                .build();

        mockMvc.perform(post("/api/v1/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("refreshToken, expected success")
    void refreshToken() throws Exception {
        var response = RefreshTokenResponse.builder()
                .accessToken("ACCESS-TOKEN")
                .refreshToken("REFRESH-TOKEN")
                .build();

        doReturn(response)
                .when(refreshTokenService)
                .refreshToken(any());

        var request = RefreshTokenRequest.builder()
                .refreshToken("OLD-REFRESH")
                .build();

        mockMvc.perform(post("/api/v1/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.accessToken").value("ACCESS-TOKEN"))
                .andExpect(jsonPath("$.refreshToken").value("REFRESH-TOKEN"))
                .andDo(print());
    }

    @Test
    @DisplayName("refreshToken, expected BadRequest")
    void refreshTokenBadRequest() throws Exception {
        var request = RefreshTokenRequest.builder()
                .refreshToken("")
                .build();

        mockMvc.perform(post("/api/v1/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}