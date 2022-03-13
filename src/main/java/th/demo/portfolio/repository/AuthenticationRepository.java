package th.demo.portfolio.repository;

import th.demo.portfolio.model.BaseUserModel;
import th.demo.portfolio.model.redis.AccessTokenRedis;
import th.demo.portfolio.model.redis.RefreshTokenRedis;

public interface AuthenticationRepository {
    void saveAccessTokenHashToRedis(String hashAccessToken, BaseUserModel baseUserModel, long expTime);
    void saveRefreshTokenHashToRedis(String hashRefreshToken, String hashAccessToken, BaseUserModel baseUserModel, long expTime);
    AccessTokenRedis getAccessTokenDetail(String accessToken);
    RefreshTokenRedis getRefreshTokenDetail(String refreshToken);
}
