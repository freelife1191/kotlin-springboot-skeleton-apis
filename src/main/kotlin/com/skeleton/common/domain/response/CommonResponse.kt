package com.skeleton.common.domain.response

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus

/**
 * Created by LYT to 2021/04/13
 */
@Schema(title = "공통 응답")
class CommonResponse<T>(
    @field:Schema(title = "응답 코드", example = "200")
    val code: Int,
    @field:Schema(title = "응답 메세지", example = "SUCCESS")
    val message: String,
    @field:Schema(title = "응답 데이터")
    var content: T?
) {

    constructor() : this(HttpStatus.OK.value(), HttpStatus.OK.reasonPhrase, null)

    constructor(content: T) : this(HttpStatus.OK.value(), HttpStatus.OK.reasonPhrase, content)

    constructor(httpStatus: HttpStatus, message: String? = null) : this(httpStatus.value(), message ?: httpStatus.reasonPhrase, null)

    constructor(httpStatus: HttpStatus, content: T) : this(httpStatus.value(), httpStatus.reasonPhrase, content)

    constructor(res: CommonFeignClientResponse<T>): this(res.code, res.message, res.content)

    override fun toString(): String {
        return "CommonResponse(code=$code, message='$message', content=$content)"
    }


}