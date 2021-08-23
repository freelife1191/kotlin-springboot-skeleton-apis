package com.skeleton.common.component.security.domain

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Created by LYT to 2021/04/28
 */
@Schema(title = "Jwt Token 정보", hidden = true)
class JwtToken(
    @Schema(title = "staff ID")
    var staffId: String,

    @Schema(title = "company ID")
    var companyId: String,

    @Schema(title = "스토어 ID")
    var storeIds: List<String>,

    @Schema(title = "공급사 ID")
    var supplierIds: List<String>,

    @field:Schema(title = "등급 : 사용자(USER), 관리자(MASTER) 소유자(OWNER) 시스템관리자(SYSTEM)")
    var grade: String? = null,

    @field:Schema(title = "직원명")
    var name: String? = null,

    @field:Schema(title = "만료기간")
    var expireDate: String? = null,

    @Schema(title = "Refresh Token")
    var refreshToken: String,
)