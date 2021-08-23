package com.skeleton.common.exception

import com.skeleton.common.exception.domain.enums.ErrorCode
import org.springframework.http.HttpStatus

/**
 * Created by KMS on 2021/06/04.
 */
class FeignClientException: CommonException {
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