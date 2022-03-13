package th.demo.portfolio.configuration.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties("username-password")
public class UsernamePasswordProperty {
    private Map<String, String> list = Map.of();
}
