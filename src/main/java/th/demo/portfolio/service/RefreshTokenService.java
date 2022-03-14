package th.demo.portfolio.service;

import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import th.demo.portfolio.component.JWTComponent;
import th.demo.portfolio.component.SHAComponent;
import th.demo.portfolio.configuration.property.JwtProperty;
import th.demo.portfolio.exception.UnauthorizedException;
import th.demo.portfolio.model.BaseUserModel;
import th.demo.portfolio.model.inbound.request.RefreshTokenRequest;
import th.demo.portfolio.model.inbound.response.RefreshTokenResponse;
import th.demo.portfolio.repository.AuthenticationRedisRepository;

@Slf4j
@Service
public class RefreshTokenService {

    private final AuthenticationRedisRepository authRedisRepository;
    private final JwtProperty property;
    private final SHAComponent shaComponent;
    private final JWTComponent jwtComponent;

    public RefreshTokenService(AuthenticationRedisRepository authRedisRepository, JwtProperty property, SHAComponent shaComponent, JWTComponent jwtComponent) {
        this.authRedisRepository = authRedisRepository;
        this.property = property;
        this.shaComponent = shaComponent;
        this.jwtComponent = jwtComponent;
    }

    @SneakyThrows
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        var hashRefreshToken = shaComponent.toSHA256String(request.getRefreshToken());
        var refreshTokenRedis = authRedisRepository.getRefreshTokenDetail(hashRefreshToken);

        if (refreshTokenRedis == null)
            throw new UnauthorizedException("Token not found.");

        var hashAccessToken = refreshTokenRedis.getAccessTokenHash();
        // delete old token
        authRedisRepository.deleteOldToken(hashAccessToken, hashRefreshToken);

        var accessTokenExpire = property.getExpire().getAccess();
        var refreshTokenExpire = property.getExpire().getRefresh();

        var username = jwtComponent.getClaimFromToken(request.getRefreshToken(), Claims::getSubject);
        var newAccessToken = jwtComponent.generateToken(username, accessTokenExpire);
        var newRefreshToken = jwtComponent.generateToken(username, refreshTokenExpire);

        var baseUserModel = BaseUserModel.builder()
                .firstName(Double.toString(Math.random()))
                .lastName(Double.toString(Math.random()))
                .build();

        authRedisRepository.saveAccessTokenHashToRedis(newAccessToken, baseUserModel, accessTokenExpire);
        authRedisRepository.saveRefreshTokenHashToRedis(newRefreshToken, newAccessToken,baseUserModel, refreshTokenExpire);

        log.info("refresh token to redis completed.");

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
