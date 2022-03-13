package th.demo.portfolio.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class RestExceptionResolver extends AbstractHandlerExceptionResolver {

    @Override
    protected ModelAndView doResolveException(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {
        try {
            log.info("RestExceptionResolver ......");

            if (ex instanceof UnauthorizedException) {
                processResponse(
                        "X-UNAUTHORIZED",
                        ex.getMessage(),
                        HttpStatus.UNAUTHORIZED.value(),
                        response);
            } else if (ex instanceof CustomException) {
                processResponse(
                        "X-CUSTOM",
                        ex.getMessage(),
                        HttpStatus.UNPROCESSABLE_ENTITY.value(),
                        response);
            } else {
                processResponse(
                        "X-OTHER",
                        ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        response);
                logger.warn("Handling of [" + ex.getClass().getName() + "] resulted in Exception", ex);
            }
            return new ModelAndView();
        } catch (Exception handlerException) {
            logger.warn("Handling of [" + ex.getClass().getName() + "] resulted in Exception", handlerException);
        }
        return null;
    }

    @SneakyThrows
    private void processResponse(String code, String message, int statusCode, HttpServletResponse response) {
        var errorModel = ErrorModel.builder()
                .code(code)
                .message(message)
                .build();
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        var mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), errorModel);
    }
}