package zerobase.weatherproject.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static zerobase.weatherproject.type.ErrorCode.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler({NotFoundException.class})
    public DiaryException handleNotFound() {
        logger.error("Not Found Exception");
        return new DiaryException(INVALID_PARAMETER_ERROR,
                INVALID_PARAMETER_ERROR.getDescription());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public DiaryException handleTypeMismatchException() {
        logger.error("Method Argument Type Mismatch Exception");
        return new DiaryException(INVALID_DATE_TYPE_ERROR,
                INVALID_DATE_TYPE_ERROR.getDescription());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public DiaryException handleMissingServletRequestParameterException() {
        logger.error("Missing Servlet Request Parameter Exception");
        return new DiaryException(INVALID_DATE_VALUE,
                INVALID_DATE_VALUE.getDescription());
    }

    @ExceptionHandler(Exception.class)
    public Exception handleAllException(Exception e) {
        logger.error("Exception -> " + e.getMessage());
        return new DiaryException(INTERNAL_SERVER_ERROR,
                INTERNAL_SERVER_ERROR.getDescription());
    }
}
