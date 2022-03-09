package th.demo.auth.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Data
@Component
@RequestScope
public class ApiContext {
    private String username;
    private String role;
    private String language;
}
