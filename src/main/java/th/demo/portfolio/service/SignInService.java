package th.demo.portfolio.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import th.demo.portfolio.component.JWTComponent;
import th.demo.portfolio.configuration.property.JwtProperty;
import th.demo.portfolio.configuration.property.UsernamePasswordProperty;
import th.demo.portfolio.exception.UnauthorizedException;
import th.demo.portfolio.model.BaseUserModel;
import th.demo.portfolio.model.inbound.request.SignInRequest;
import th.demo.portfolio.model.inbound.response.SignInResponse;
import th.demo.portfolio.repository.AuthenticationRedisRepository;

@Slf4j
@Service
public class SignInService {

    private final UsernamePasswordProperty usernamePasswordProperty;
    private final JWTComponent jwtComponent;
    private final JwtProperty property;
    private final AuthenticationRedisRepository authRedisRepository;

    public SignInService(UsernamePasswordProperty usernamePasswordProperty, JWTComponent jwtComponent, JwtProperty property, AuthenticationRedisRepository authRedisRepository) {
        this.usernamePasswordProperty = usernamePasswordProperty;
        this.jwtComponent = jwtComponent;
        this.property = property;
        this.authRedisRepository = authRedisRepository;
    }

    @SneakyThrows
    public SignInResponse signIn(SignInRequest request) {
        var username = request.getUsername();
        var password = request.getPassword();
        var master = usernamePasswordProperty.getList();

        var expectedPassword = master.get(username);

        if (password.equals(expectedPassword)) {
            var accessTokenExpire = property.getExpire().getAccess();
            var refreshTokenExpire = property.getExpire().getRefresh();

            var accessToken = jwtComponent.generateToken(username, accessTokenExpire);
            var refreshToken = jwtComponent.generateToken(username, refreshTokenExpire);

            var baseUserModel = BaseUserModel.builder()
                    .firstName(username + "-FIRSTNAME")
                    .lastName(username + "-LASTNAME")
                    .age((int) (Math.random() * 10))
                    .build();

            authRedisRepository.saveAccessTokenHashToRedis(accessToken, baseUserModel, accessTokenExpire);
            authRedisRepository.saveRefreshTokenHashToRedis(refreshToken, accessToken,baseUserModel, refreshTokenExpire);

            log.info("store signIn token to redis completed.");

            return SignInResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } else {
            throw new UnauthorizedException("Invalid username or password");
        }
    }
}
