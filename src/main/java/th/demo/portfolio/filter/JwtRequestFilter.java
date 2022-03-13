package th.demo.portfolio.filter;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import th.demo.portfolio.component.JWTComponent;
import th.demo.portfolio.configuration.property.BypassApiProperty;
import th.demo.portfolio.exception.RestExceptionResolver;
import th.demo.portfolio.exception.UnauthorizedException;
import th.demo.portfolio.model.ApiContext;
import th.demo.portfolio.model.redis.AccessTokenRedis;
import th.demo.portfolio.repository.AuthenticationRepository;

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
    private final JWTComponent JwtComponent;
    private final RestExceptionResolver resolver;
    private final AuthenticationRepository authRepository;

    public JwtRequestFilter(ApiContext apiContext, BypassApiProperty bypassApiProperty, JWTComponent JwtComponent, RestExceptionResolver resolver, AuthenticationRepository authRepository) {
        this.apiContext = apiContext;
        this.bypassApiProperty = bypassApiProperty;
        this.JwtComponent = JwtComponent;
        this.resolver = resolver;
        this.authRepository = authRepository;
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
            } catch (UnauthorizedException ex) {
                resolver.resolveException(request, response, null, ex);
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
        var accessTokenRedis = authRepository.getAccessTokenDetail(token);

        if (accessTokenRedis == null)
            throw new UnauthorizedException("Not found, Invalid token.");

        var claims = JwtComponent.getAllClaimsFromToken(token);

        initApiContext(request, claims, token, accessTokenRedis);

        filterChain.doFilter(request, response);
    }

    private void initApiContext(HttpServletRequest request, Claims claims, String token, AccessTokenRedis accessTokenRedis) {
        apiContext.setUsername((String) claims.get("username"));
        apiContext.setAuthenticationHeader(token);
        apiContext.setBaseUserModel(accessTokenRedis.getBaseUserModel());

        var language = ("th-TH").equals(request.getHeader("Accept-Language")) ? "th" : "en";
        apiContext.setLanguage(language);
    }
}
