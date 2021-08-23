package com.skeleton.common.component.excel.api.sample.controller

import com.skeleton.common.BaseMvcTest
import com.skeleton.common.component.excel.api.sample.domain.SampleExcelUpload
import com.skeleton.common.domain.response.CommonResponse
import com.skeleton.common.exception.domain.enums.ErrorCode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*
import javax.validation.ConstraintViolation
import javax.validation.Validator

/**
 * 엑셀 공통 서비스 테스트
 * Created by KMS on 2021/07/31.
 */
class SampleExcelControllerTest(val validator: Validator): BaseMvcTest() {

    val DEFAULT_API_URL = "/tutorial/excel"

    @BeforeEach
    fun beforeSetUp() {
        applyEncodingFilter()
    }

    @Test
    fun `엑셀 다운로드 테스트`() {
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(DEFAULT_API_URL))
            .andExpect(status().isOk)
            .andDo(print())
    }

    @Test
    fun `엑셀 양식 다운로드 테스트`() {
        this.mockMvc
            .perform(MockMvcRequestBuilders.get("$DEFAULT_API_URL/form"))
            .andExpect(status().isOk)
            .andDo(print())
    }

    @Test
    fun `Sample 엑셀 업로드 성공 테스트`() {
        val builder = MockMvcRequestBuilders.multipart(DEFAULT_API_URL)
            .file(getMultipartFile("sampleUpload.xlsx", "file"))

        val result = mockMvc.perform(builder)
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(HttpStatus.OK.value()))
            .andExpect(jsonPath("message").value(HttpStatus.OK.reasonPhrase))
            .andDo(print())

        val responseBody = result.andReturn().response.contentAsString
        val response: CommonResponse<*> = mapper.readValue(responseBody, CommonResponse::class.java)
        val sampleExcelList = response.content as List<Map<String, Any>>
        for (dto in sampleExcelList) {
            println(dto)
        }
    }

    @Test
    fun `Sample 엑셀 업로드 실패 테스트`() {
        val builder = MockMvcRequestBuilders.multipart(DEFAULT_API_URL)
            .file(getMultipartFile("sampleUpload_error.xlsx", "file"))

        mockMvc.perform(builder)
            .andExpect(status().isOk)
            .andExpect(jsonPath("code").value(ErrorCode.EXCEL_READER_FIELD_ERROR.code))
            .andDo(print())
    }

    @Test
    fun `객체 Validation 체크`() {
        val constraintValidations: Set<ConstraintViolation<SampleExcelUpload>> = validator.validate(SampleExcelUpload())
        val validData: ConstraintViolation<SampleExcelUpload> =
            constraintValidations.first { it.propertyPath.toString() == "name" }
        if (Objects.nonNull(validData)) {
            println(validData.messageTemplate.contains("NotEmpty") || validData.messageTemplate.contains("NotNull"))
            println("${validData.propertyPath} : ${validData.message}");
        }

        for(data in constraintValidations) {
            println(data.propertyPath);
        }

        println("## constraintValidations = $constraintValidations");
        println("## size = ${constraintValidations.size}");
        println("## message = ${constraintValidations.iterator().next().message}");
    }
}