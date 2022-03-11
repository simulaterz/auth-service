package th.demo.auth.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import th.demo.auth.model.ApiContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenComponent {

    @Value("${jwt.secret}")
    private String secret;

    private final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
    private final ApiContext apiContext;

    public JwtTokenComponent(ApiContext apiContext) {
        this.apiContext = apiContext;
    }

    public String generateToken() {
        var claims = new HashMap<String, Object>();
        claims.put("username", apiContext.getUsername());
        claims.put("role", apiContext.getRole());
        claims.put("language", apiContext.getLanguage());

        return doGenerateToken(claims, apiContext.getUsername());
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        var claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY*1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        var expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

}
