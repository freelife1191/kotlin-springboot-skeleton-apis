package com.skeleton.auth.client

import com.skeleton.common.component.security.domain.process.ValidTokenInfo
import com.skeleton.common.domain.response.CommonResponse
import javax.servlet.http.HttpServletRequest

/**
 * Created by LYT to 2021/06/23
 */
interface AuthClient {

    /**
     * 토큰 검증
     * @param req HttpServletRequest
     * @return CommonResponse<ValidTokenInfo>
     */
    fun validToken(req: HttpServletRequest): CommonResponse<ValidTokenInfo>

    /**
     * 토큰 검증
     * @param token String
     * @return CommonResponse<ValidTokenInfo>
     */
    fun validToken(token: String): CommonResponse<ValidTokenInfo>
}