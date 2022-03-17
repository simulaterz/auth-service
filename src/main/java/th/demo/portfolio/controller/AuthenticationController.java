package th.demo.portfolio.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th.demo.portfolio.model.inbound.request.RefreshTokenRequest;
import th.demo.portfolio.model.inbound.request.SignInRequest;
import th.demo.portfolio.model.inbound.request.SignOutRequest;
import th.demo.portfolio.model.inbound.response.RefreshTokenResponse;
import th.demo.portfolio.model.inbound.response.SignInResponse;
import th.demo.portfolio.service.RefreshTokenService;
import th.demo.portfolio.service.SignInService;
import th.demo.portfolio.service.SignOutService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final SignInService signInService;
    private final SignOutService signOutService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationController(SignInService signInService, SignOutService signOutService, RefreshTokenService refreshTokenService) {
        this.signInService = signInService;
        this.signOutService = signOutService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping(value = "/signIn")
    public SignInResponse signIn(@Valid @RequestBody SignInRequest request) {
        return signInService.signIn(request);
    }

    @PostMapping(value = "/signOut")
    public void signOut(@Valid @RequestBody SignOutRequest request) {
        signOutService.signOut(request);
    }

    @PostMapping(value = "/refreshToken")
    public RefreshTokenResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return refreshTokenService.refreshToken(request);
    }
}
