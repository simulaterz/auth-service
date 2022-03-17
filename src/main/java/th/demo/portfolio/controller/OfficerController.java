package th.demo.portfolio.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th.demo.portfolio.model.ApiContext;
import th.demo.portfolio.model.OfficerModel;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/officer")
public class OfficerController {

    private final ApiContext apiContext;

    public OfficerController(ApiContext apiContext) {
        this.apiContext = apiContext;
    }

    @GetMapping("/inquiry")
    public OfficerModel inquiryOfficer() {
        // use cached data on api context
        var baseUserModel = apiContext.getBaseUserModel();

        return OfficerModel.builder()
                .firstName(baseUserModel.getFirstName())
                .lastName(baseUserModel.getLastName())
                .age(baseUserModel.getAge())
                .position("developer")
                .skill("Java, Kotlin")
                .build();
    }
}
