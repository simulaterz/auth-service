package th.demo.portfolio.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th.demo.portfolio.component.JWTComponent;

@Slf4j
@RestController
@RequestMapping(value = "/v1/auth")
public class AuthController {

    private final JWTComponent JWTComponent;

    public AuthController(JWTComponent JWTComponent) {
        this.JWTComponent = JWTComponent;
    }

    @GetMapping("/sign-in")
    public String signIn() {
        log.info("trigger sign in");
        return JWTComponent.generateToken("mock", "!@#$");
    }
}
