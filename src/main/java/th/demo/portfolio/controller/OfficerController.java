package th.demo.portfolio.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th.demo.portfolio.model.ApiContext;
import th.demo.portfolio.model.OfficerModel;

@Slf4j
@RestController
@RequestMapping(value = "/v1/officer")
public class OfficerController {

    private final ApiContext apiContext;

    public OfficerController(ApiContext apiContext) {
        this.apiContext = apiContext;
    }

    @GetMapping("/inquiry")
    public OfficerModel inquiryOfficer() {
        log.info("trigger inquiry");
        log.info("contextProfile = {}", apiContext);

        return OfficerModel.builder()
                .firstName("john")
                .lastName("doe")
                .age(20)
                .station("bangkok")
                .floor(12)
                .build();
    }
}
