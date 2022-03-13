package th.demo.portfolio.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Data
@Builder
@Component
@RequestScope
@NoArgsConstructor
@AllArgsConstructor
public class ApiContext {
    private String authenticationHeader;
    private String username;
    private String role;
    private String language;
}
