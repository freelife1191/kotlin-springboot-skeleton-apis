package com.skeleton.common.component.excel.api.sample.domain

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * Created by KMS on 2021/07/30.
 */
@Schema(title = "Sample 엑셀 업로드 객체", hidden = true)
class SampleExcelUpload(
    @field:NotEmpty
    @field:Schema(title = "이름")
    var name: String? = null,
    @field:NotEmpty
    @field:Schema(title = "이메일")
    var email: String? = null,
    @field:NotEmpty
    @field:Schema(title = "전화번호")
    var phone: String? = null,
    @field:NotEmpty
    @field:Schema(title = "소속부서")
    var dept: String? = null,
    @field:NotNull
    @field:Schema(title = "업무코드")
    var workCode: Int? = null,
    @field:NotNull
    @field:Schema(title = "부서코드")
    var deptCode: Int? = null,
    @field:Size(min = 1, max = 500)
    @field:Schema(title = "내용")
    var content: String? = null,
    @field:Size(max = 15)
    @field:Pattern(regexp="(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])", message = "IP 주소 형식이 올바르지 않습니다")
    @field:Schema(title = "IP")
    var ip: String? = null,
    @field:Schema(title = "소수")
    var percent: BigDecimal? = null,
    @field:Schema(title = "날짜")
    var createDate: String? = null,
    @field:Schema(title = "일시")
    var updateDatetime: String? = null,
    @field:Schema(title = "빈데이터")
    var empty: String? = null,
) {

    override fun toString(): String {
        return "SampleExcelUpload(name=$name, email=$email, phone=$phone, dept=$dept, workCode=$workCode, deptCode=$deptCode, content=$content, ip=$ip, percent=$percent, createDate=$createDate, updateDatetime=$updateDatetime, empty=$empty)"
    }
}