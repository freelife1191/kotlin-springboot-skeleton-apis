package com.skeleton.common.component.security.domain.process

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Created by LYT to 2021/08/03
 */
@Schema(title = "토큰 인증 정보")
class ValidTokenInfo(

    @Schema(title = "토큰 정보")
    val tokenInfo: HashMap<String, Any>?,

    @Schema(title = "토큰")
    val token: String
)