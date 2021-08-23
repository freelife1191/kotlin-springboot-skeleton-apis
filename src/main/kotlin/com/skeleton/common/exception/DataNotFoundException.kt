package com.skeleton.common.exception

import com.skeleton.common.exception.domain.enums.ErrorCode
import org.springframework.http.HttpStatus

/**
 * Created by KMS on 2021/05/11.
 */
class DataNotFoundException: CommonException {

    constructor(errorMessage: String) : super(ErrorCode.DATA_NOT_FOUND, errorMessage)
    constructor(errorCode: ErrorCode) : super(errorCode)
    constructor(errorCode: ErrorCode, httpStatus: HttpStatus) : super(errorCode, httpStatus)
    constructor(httpStatus: HttpStatus, errorMessage: String) : super(httpStatus, errorMessage)
    constructor() : super()
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
        message,
        cause,
        enableSuppression,
        writableStackTrace
    )
}