package th.demo.portfolio.repository.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import th.demo.portfolio.component.SHAComponent;
import th.demo.portfolio.configuration.property.JwtProperty;
import th.demo.portfolio.model.BaseUserModel;
import th.demo.portfolio.model.redis.AccessTokenRedis;
import th.demo.portfolio.model.redis.RefreshTokenRedis;
import th.demo.portfolio.repository.AuthenticationRepository;
import th.demo.portfolio.repository.RedisClient;

@Slf4j
@Repository
public class AuthenticationRepositoryImpl implements AuthenticationRepository {

    private final RedisClient redisClient;
    private final JwtProperty jwtProperty;
    private final SHAComponent shaComponent;

    public AuthenticationRepositoryImpl(RedisClient redisClient, JwtProperty jwtProperty, SHAComponent shaComponent) {
        this.redisClient = redisClient;
        this.jwtProperty = jwtProperty;
        this.shaComponent = shaComponent;
    }

    @Override
    @SneakyThrows
    public void saveAccessTokenHashToRedis(String accessToken, BaseUserModel baseUserModel, long expTime) {
        var hashAccessToken = shaComponent.toSHA256String(accessToken);
        var key = jwtProperty.getKey().getAccess() + hashAccessToken;
        var value = AccessTokenRedis.builder()
                .baseUserModel(baseUserModel)
                .build();
        this.redisClient.setObject(key, value, expTime);
    }

    @Override
    @SneakyThrows
    public void saveRefreshTokenHashToRedis(String refreshToken, String hashAccessToken, BaseUserModel baseUserModel, long expTime) {
        var hashRefreshToken = shaComponent.toSHA256String(refreshToken);
        var key = jwtProperty.getKey().getRefresh() + hashRefreshToken;
        var value = RefreshTokenRedis.builder()
                .accessTokenHash(hashAccessToken)
                .baseUserModel(baseUserModel)
                .build();
        this.redisClient.setObject(key, value, expTime);
    }

    @Override
    @SneakyThrows
    public AccessTokenRedis getAccessTokenDetail(String accessToken) {
        var hashAccessToken = shaComponent.toSHA256String(accessToken);
        var key = jwtProperty.getKey().getAccess() + hashAccessToken;
        return this.redisClient.getObjectByKey(key, AccessTokenRedis.class);
    }

    @Override
    @SneakyThrows
    public RefreshTokenRedis getRefreshTokenDetail(String refreshToken) {
        var hashRefreshToken = shaComponent.toSHA256String(refreshToken);
        var key = jwtProperty.getKey().getRefresh() + hashRefreshToken;
        return this.redisClient.getObjectByKey(key, RefreshTokenRedis.class);
    }
}
