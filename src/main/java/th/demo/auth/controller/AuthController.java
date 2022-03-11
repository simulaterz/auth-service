package th.demo.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th.demo.auth.component.JwtTokenComponent;
import th.demo.auth.model.OfficerModel;

@Slf4j
@RestController
@RequestMapping(value = "/v1/auth")
public class AuthController {

    private final JwtTokenComponent jwtTokenComponent;

    public AuthController(JwtTokenComponent jwtTokenComponent) {
        this.jwtTokenComponent = jwtTokenComponent;
    }

    @GetMapping("/sign-in")
    public String signIn() {
        log.info("trigger sign in");
        return jwtTokenComponent.generateToken("mock", "!@#$");
    }
}
