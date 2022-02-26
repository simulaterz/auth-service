package th.demo.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th.demo.auth.model.OfficerModel;

@Slf4j
@RestController
@RequestMapping(value = "/v1")
public class UserController {

    @GetMapping("/officer/inquiry")
    public OfficerModel inquiryOfficer() {

        log.info("trigger inquiry");

        return OfficerModel.builder()
                .firstName("john")
                .lastName("doe")
                .age(20)
                .station("bangkok")
                .floor(12)
                .build();
    }
}
