package th.demo.portfolio.repository;

import th.demo.portfolio.model.BaseUserModel;
import th.demo.portfolio.model.redis.AccessTokenRedis;
import th.demo.portfolio.model.redis.RefreshTokenRedis;

public interface AuthenticationRedisRepository {
    void saveAccessTokenHashToRedis(String accessToken, BaseUserModel baseUserModel, long expTime);
    void saveRefreshTokenHashToRedis(String refreshToken, String accessToken, BaseUserModel baseUserModel, long expTime);
    AccessTokenRedis getAccessTokenDetail(String hashAccessToken);
    RefreshTokenRedis getRefreshTokenDetail(String hashRefreshToken);
    void deleteOldToken(String hashAccessToken, String hashRefreshToken);
}
