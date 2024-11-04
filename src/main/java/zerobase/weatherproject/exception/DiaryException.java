package zerobase.weatherproject.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import zerobase.weatherproject.type.ErrorCode;

@Getter
@Setter
@AllArgsConstructor
public class DiaryException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String message;

    public DiaryException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getDescription();
    }
}
