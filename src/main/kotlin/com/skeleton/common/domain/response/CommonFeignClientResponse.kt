package com.skeleton.common.domain.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.swagger.v3.oas.annotations.media.Schema

/**
 * Created by KMS on 2021/06/04.
 */
@Schema(title = "Feign Client 공통 응답")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class CommonFeignClientResponse<T> (
    @field:Schema(title = "응답 코드", example = "200")
    var code: Int,
    @field:Schema(title = "응답 메세지", example = "SUCCESS")
    var message: String,
    @field:Schema(title = "응답 데이터")
    var content: T?
) {
    override fun toString(): String {
        return "CommonFeignClientResponse(code=$code, message='$message', content=$content)"
    }
}