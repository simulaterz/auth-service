package th.demo.auth.fiter;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import th.demo.auth.component.JwtTokenComponent;
import th.demo.auth.exception.RestExceptionResolver;
import th.demo.auth.exception.UnauthorizedException;
import th.demo.auth.model.ApiContext;
import th.demo.auth.property.BypassApiProperty;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final ApiContext apiContext;
    private final BypassApiProperty bypassApiProperty;
    private final JwtTokenComponent jwtTokenComponent;
    private final RestExceptionResolver resolver;

    public JwtRequestFilter(ApiContext apiContext, BypassApiProperty bypassApiProperty, JwtTokenComponent jwtTokenComponent, RestExceptionResolver resolver) {
        this.apiContext = apiContext;
        this.bypassApiProperty = bypassApiProperty;
        this.jwtTokenComponent = jwtTokenComponent;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var isMatchBypass = bypassApiProperty.getAuthorization()
                .stream()
                .anyMatch(bypass -> new AntPathMatcher().match(bypass, request.getServletPath()));

        log.info("Filter isMatchBypass: {}", isMatchBypass);

        if (!isMatchBypass) {
            try {
                processRequestHeader(request, response, filterChain);
            } catch (Exception ex) {
                ex.printStackTrace();
                resolver.resolveException(request, response, null, new UnauthorizedException("Invalid token."));
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private String authorizationHeaderToJWTString(String authorization) {
        if (authorization != null) {
            return authorization.substring("Bearer ".length());
        }
        return null;
    }

    private void processRequestHeader(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        var token = authorizationHeaderToJWTString(authorization);
        var claims = jwtTokenComponent.getAllClaimsFromToken(token);

        initApiContext(request, claims);

        filterChain.doFilter(request, response);
    }

    private void initApiContext(HttpServletRequest request, Claims claims) {
        apiContext.setUsername((String) claims.get("username"));
        apiContext.setRole((String) claims.get("role"));

        var language = ("th-TH").equals(request.getHeader("Accept-Language")) ? "th" : "en";
        apiContext.setLanguage(language);
    }
}
