package com.skeleton.common.utils

import com.skeleton.common.domain.response.CommonFeignClientResponse
import com.skeleton.common.domain.response.CommonResponse
import com.skeleton.common.exception.domain.enums.ErrorCode
import com.skeleton.common.exception.utils.ErrorUtils
import org.springframework.http.HttpStatus

/**
 * Created by KMS on 2021/06/11.
 */
class FeignClientUtils {

    companion object {
        /**
         * FeignClient 에서 응답온 데이터를 맵핑한다
         * EX: FeignClientUtils.responseMapper(storeFeignClient.findProductCodes()) as CommonFeignClientResponse<List<String>>
         * @param res CommonFeignClientResponse<T>
         * @return CommonResponse<T>
         */
        fun <T> responseMapper(res: CommonFeignClientResponse<T>): CommonResponse<T> {
            if(res.code == HttpStatus.OK.value() || res.code == HttpStatus.CREATED.value())
                return CommonResponse(res.content as T)
            ErrorCode.getErrorCode(res.code)

            ErrorUtils.errorWriter("FeignClientUtils", ErrorUtils.getResErrorDTO(res.code), res.message, res.content as Map<String, Any>, "")
            return CommonResponse(HttpStatus.valueOf(res.code), res.message)
        }
    }
}