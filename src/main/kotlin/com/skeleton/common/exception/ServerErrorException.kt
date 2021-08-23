package com.skeleton.common.exception

import com.skeleton.common.exception.domain.enums.ErrorCode
import org.springframework.http.HttpStatus

/**
 * Exception 커스텀 예외 클래스 설계
 * https://jsonobject.tistory.com/501
 *
 * 사전에 개발자가 의도한 예외를 설계할 차례이다. 오류 코드, 오류 메시지, HTTP 응답 상태 코드 3개를 담도록 설계했다
 * 개발자는 비지니스 로직에서 적절한 오류 코드를 담아 이 예외를 발생시키면, 아래 설명할 @ControllerAdvice가 적절한 오류 응답을 하게 된다
 *
 * Created by KMS on 2021/05/08.
 */
class ServerErrorException: CommonException {
    constructor(errorCode: ErrorCode) : super(errorCode)
    constructor(errorCode: ErrorCode, httpStatus: HttpStatus) : super(errorCode, httpStatus)
    constructor(errorCode: ErrorCode, errorMessage: String) : super(errorCode, errorMessage)
    constructor(errorCode: ErrorCode, errorMessage: String, errorData: String) : super(
        errorCode,
        errorMessage,
        errorData
    )

    constructor(httpStatus: HttpStatus, errorMessage: String) : super(httpStatus, errorMessage)
    constructor(httpStatus: HttpStatus, errorMessage: String, errorData: String) : super(
        httpStatus,
        errorMessage,
        errorData
    )

    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
        message,
        cause,
        enableSuppression,
        writableStackTrace
    )
}