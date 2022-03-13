package th.demo.portfolio.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import th.demo.portfolio.configuration.property.JwtProperty;

import java.time.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("Test -> JWTComponent")
class JWTComponentTest {

    @InjectMocks
    private JWTComponent component;

    @Mock
    private Clock clock;

    @Spy
    private JwtProperty property = new JwtProperty();

    private final LocalDateTime DATE_TIME = LocalDate.of(2022, 3, 10).atTime(10, 1);
    private final long FIVE_HOURS = 5 * 60 * 60 * 1000;
    private Clock fixedClock;
    private String globalToken;

    @BeforeEach
    public void setUp() {
        var expire = JwtProperty.JwtExpireProperty.builder()
                .access(FIVE_HOURS)
                .refresh(FIVE_HOURS)
                .build();

        property.setSecret("jwtsecret");
        property.setExpire(expire);

        fixedClock = Clock.fixed(DATE_TIME.toInstant(ZoneOffset.UTC), ZoneId.of("Asia/Bangkok"));
        doReturn(fixedClock.millis()).when(clock).millis();
    }

    @Test
    @DisplayName("generate and validate token, expected success")
    void generateAndValidateToken() {
        // set global token
        globalToken = component.generateToken("my-user", FIVE_HOURS);
        var result = component.getAllClaimsFromToken(globalToken);

        var expiryDate = fixedClock.instant();
        var expectedExpiryDate = Instant.ofEpochMilli(expiryDate.toEpochMilli() + FIVE_HOURS);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedExpiryDate, result.getExpiration().toInstant());
        Assertions.assertEquals("my-user", (String) result.get("username"));;
    }

    @Test
    @DisplayName("get claim from token, expected success")
    void getClaimFromToken() {
        generateAndValidateToken();
        var issuer = component.getClaimFromToken(globalToken, Claims::getSubject);

        Assertions.assertEquals("my-user", issuer);
    }

    @Test
    @DisplayName("validate expired token, expected failed")
    void validateExpiredToken() {
        generateAndValidateToken();

        // prepare
        var sixHours = 6 * 60 * 60 * 1000;
        var invalidDate = fixedClock.millis() + sixHours;
        doReturn(invalidDate).when(clock).millis();

        ExpiredJwtException exception = assertThrows(ExpiredJwtException.class, () -> {
            component.getAllClaimsFromToken(globalToken);
        });

        Assertions.assertTrue(exception.getMessage().contains("a difference of 3600000 milliseconds"));
    }
}