package th.demo.portfolio.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenComponent {

    @Value("${jwt.secret}")
    private String secret;

    private final Clock clock;
    private final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    public JwtTokenComponent(Clock clock) {
        this.clock = clock;
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
                .setExpiration(new Date(clock.millis() + JWT_TOKEN_VALIDITY*1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .setClock(() -> new Date(clock.millis()))
                .parseClaimsJws(token)
                .getBody();
    }
}
