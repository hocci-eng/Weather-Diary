package zerobase.weatherproject.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    FAILED_API_REQUEST("api request 실패 오류입니다."),
    FAILED_JSON_PARSING("json paring 실패 오류입니다."),
    INVALID_DATE_VALUE("파라미터 값이 누락된 오류입니다."),
    INVALID_DATE_TYPE_ERROR("잘못된 파라미터 타입 입력 오류입니다."),
    INVALID_PARAMETER_ERROR("잘못된 파라미터 요청 오류입니다."),
    FUTURE_DATE_NOT_ALLOWED("미래 파라미터값 입력 오류입니다."),
    INTERNAL_SERVER_ERROR("내부 서버 오류입니다.");

    private final String description;
}
