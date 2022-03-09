package th.demo.auth.fiter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import th.demo.auth.component.JwtTokenComponent;
import th.demo.auth.model.ApiContext;
import th.demo.auth.property.BypassApiProperty;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private ApiContext apiContext;

    @Autowired
    private BypassApiProperty bypassApiProperty;

    @Autowired
    private JwtTokenComponent jwtTokenComponent;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var isMatchBypass = bypassApiProperty.getAuthorization()
                .stream()
                .anyMatch(bypass -> new AntPathMatcher().match(bypass, request.getServletPath()));

        log.info("Filter isMatchBypass: {}", isMatchBypass);

        if (!isMatchBypass) {
            var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            var token = authorizationHeaderToJWTString(authorization);

            try {
                var validToken = jwtTokenComponent.validateToken(token);
                if (!validToken)
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                else
                    filterChain.doFilter(request, response);

            } catch (Exception ex) {
                // TODO:: Use global exception handler
                log.error("Validate JWT failed.");
                ex.printStackTrace();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            // TODO:: mock context profile
            var username = Double.toString(Math.random());
            apiContext.setUsername(username);
            apiContext.setRole("ADMIN");

            filterChain.doFilter(request, response);
        }
    }

    public String authorizationHeaderToJWTString(String authorization) {
        if(authorization != null) {
            return authorization.substring("Bearer ".length());
        }
        return null;
    }
}
