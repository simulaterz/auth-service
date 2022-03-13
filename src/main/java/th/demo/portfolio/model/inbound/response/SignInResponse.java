package th.demo.portfolio.model.inbound.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignInResponse {
    private String accessToken;
    private String refreshToken;
}
