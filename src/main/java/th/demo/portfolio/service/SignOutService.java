package th.demo.portfolio.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import th.demo.portfolio.component.SHAComponent;
import th.demo.portfolio.exception.CustomException;
import th.demo.portfolio.model.inbound.request.SignOutRequest;
import th.demo.portfolio.repository.AuthenticationRedisRepository;

@Slf4j
@Service
public class SignOutService {

    private final SHAComponent shaComponent;
    private final AuthenticationRedisRepository authRedisRepository;

    public SignOutService(SHAComponent shaComponent, AuthenticationRedisRepository authRedisRepository) {
        this.shaComponent = shaComponent;
        this.authRedisRepository = authRedisRepository;
    }

    public void signOut(SignOutRequest request) {
        try {
            var hashAccessToken = shaComponent.toSHA256String(request.getAccessToken());
            var hashRefreshToken = shaComponent.toSHA256String(request.getRefreshToken());
            authRedisRepository.deleteOldToken(hashAccessToken, hashRefreshToken);
        } catch (Exception ex) {
            throw new CustomException("Exception during signOut.");
        }

    }
}
