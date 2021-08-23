package com.skeleton.common.exception

/**
 * Created by LYT to 2021/07/02
 */
class DecryptionException: CommonException {
    constructor(cause: Throwable?): super(cause)
//    constructor(errorCode: ErrorCode, errorMessage: String) : super(errorCode, errorMessage)
}