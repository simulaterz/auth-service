package th.demo.portfolio.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("filter.bypass")
public class BypassApiProperty {
    private List<String> authorization = List.of();
}
