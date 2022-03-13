package th.demo.portfolio.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import th.demo.portfolio.property.JwtProperty;

import java.time.Clock;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTComponent {

    private final Clock clock;
    private final JwtProperty jwtProperty;

    public JWTComponent(Clock clock, JwtProperty jwtProperty) {
        this.clock = clock;
        this.jwtProperty = jwtProperty;
    }

    public String generateToken(String username, String role) {
        var claims = new HashMap<String, Object>();
        claims.put("username", username);
        claims.put("role", role);

        return doGenerateToken(claims, username);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        var claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(clock.millis()))
                .setExpiration(new Date(clock.millis() + jwtProperty.getExpireMillis()))
                .signWith(SignatureAlgorithm.HS512, jwtProperty.getSecret())
                .compact();
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperty.getSecret())
                .setClock(() -> new Date(clock.millis()))
                .parseClaimsJws(token)
                .getBody();
    }
}
