package com.skeleton.common.component.excel.exception

import com.skeleton.common.component.excel.domain.enums.ExcelErrorCode
import org.springframework.http.HttpStatus

/**
 * Created by KMS on 2021/05/26.
 */
class ExcelReaderFileExtentionException: ExcelCommonException {
    constructor(errorCode: ExcelErrorCode) : super(errorCode)
    constructor(errorCode: ExcelErrorCode, httpStatus: HttpStatus) : super(errorCode, httpStatus)
    constructor(httpStatus: HttpStatus, errorMessage: String) : super(httpStatus, errorMessage)
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