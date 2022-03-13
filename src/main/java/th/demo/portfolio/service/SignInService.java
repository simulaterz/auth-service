package th.demo.portfolio.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import th.demo.portfolio.component.JWTComponent;
import th.demo.portfolio.configuration.property.JwtProperty;
import th.demo.portfolio.configuration.property.UsernamePasswordProperty;
import th.demo.portfolio.exception.UnauthorizedException;
import th.demo.portfolio.model.inbound.request.SignInRequest;
import th.demo.portfolio.model.inbound.response.SignInResponse;

@Slf4j
@Service
public class SignInService {

    private final UsernamePasswordProperty usernamePasswordProperty;
    private final JWTComponent jwtComponent;
    private final JwtProperty property;

    public SignInService(UsernamePasswordProperty usernamePasswordProperty, JWTComponent jwtComponent, JwtProperty property) {
        this.usernamePasswordProperty = usernamePasswordProperty;
        this.jwtComponent = jwtComponent;
        this.property = property;
    }

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

            return SignInResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } else {
            throw new UnauthorizedException("Invalid username or password");
        }
    }
}
