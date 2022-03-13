package th.demo.portfolio.configuration.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("jwt")
public class JwtProperty {
    private String secret;
    private JwtExpireProperty expire;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JwtExpireProperty {
        private long access;
        private long refresh;
    }
}


