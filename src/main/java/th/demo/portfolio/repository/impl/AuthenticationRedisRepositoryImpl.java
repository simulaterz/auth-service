package th.demo.portfolio.repository.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import th.demo.portfolio.component.SHAComponent;
import th.demo.portfolio.configuration.property.JwtProperty;
import th.demo.portfolio.model.BaseUserModel;
import th.demo.portfolio.model.redis.AccessTokenRedis;
import th.demo.portfolio.model.redis.RefreshTokenRedis;
import th.demo.portfolio.repository.AuthenticationRedisRepository;
import th.demo.portfolio.repository.RedisClient;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class AuthenticationRedisRepositoryImpl implements AuthenticationRedisRepository {

    private final RedisClient redisClient;
    private final JwtProperty jwtProperty;
    private final SHAComponent shaComponent;

    public AuthenticationRedisRepositoryImpl(RedisClient redisClient, JwtProperty jwtProperty, SHAComponent shaComponent) {
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
        this.redisClient.setObject(key, value, TimeUnit.MILLISECONDS.toSeconds(expTime));
    }

    @Override
    @SneakyThrows
    public void saveRefreshTokenHashToRedis(String refreshToken, String accessToken, BaseUserModel baseUserModel, long expTime) {
        var hashRefreshToken = shaComponent.toSHA256String(refreshToken);
        var hashAccessToken = shaComponent.toSHA256String(accessToken);
        var key = jwtProperty.getKey().getRefresh() + hashRefreshToken;
        var value = RefreshTokenRedis.builder()
                .accessTokenHash(hashAccessToken)
                .baseUserModel(baseUserModel)
                .build();
        this.redisClient.setObject(key, value, TimeUnit.MILLISECONDS.toSeconds(expTime));
    }

    @Override
    @SneakyThrows
    public AccessTokenRedis getAccessTokenDetail(String hashAccessToken) {
        var key = jwtProperty.getKey().getAccess() + hashAccessToken;
        return this.redisClient.getObjectByKey(key, AccessTokenRedis.class);
    }

    @Override
    @SneakyThrows
    public RefreshTokenRedis getRefreshTokenDetail(String hashRefreshToken) {
        var key = jwtProperty.getKey().getRefresh() + hashRefreshToken;
        return this.redisClient.getObjectByKey(key, RefreshTokenRedis.class);
    }

    @Override
    @SneakyThrows
    public void deleteOldToken(String hashAccessToken, String hashRefreshToken) {
        var accessKey = jwtProperty.getKey().getAccess() + hashAccessToken;
        var refreshKey = jwtProperty.getKey().getRefresh() + hashRefreshToken;

        try {
            log.debug("Old access : {}", hashAccessToken);
            log.debug("Old refresh : {}", hashRefreshToken);

            if (hashAccessToken != null && !hashAccessToken.isEmpty()) {
                this.redisClient.del(accessKey);
            }
            if (hashRefreshToken != null && !hashRefreshToken.isEmpty()) {
                this.redisClient.del(refreshKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
