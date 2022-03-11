package th.demo.portfolio.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UnauthorizedException extends RuntimeException {
    private final String message;
}
