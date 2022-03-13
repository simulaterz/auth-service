package th.demo.portfolio.model.inbound.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignInRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
