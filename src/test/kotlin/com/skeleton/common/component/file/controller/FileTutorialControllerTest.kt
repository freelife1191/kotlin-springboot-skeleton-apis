package com.skeleton.common.component.file.controller

import com.skeleton.common.BaseMvcTest
import com.skeleton.config.S3MockConfig
import io.findify.s3mock.S3Mock
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Ignore

/**
 * Created by KMS on 2021/04/19.
 */
@ActiveProfiles("test")
@Import(S3MockConfig::class)
class FileTutorialControllerTest(
    val s3Mock: S3Mock
): BaseMvcTest() {

    val DEFAULT_API_URL = "/tutorial/file"

    @BeforeEach
    fun beforeSetUp() = applyEncodingFilter()

    @Test
    fun `단건 파일 등록 테스트`() {
        val builder: MockHttpServletRequestBuilder = MockMvcRequestBuilders.multipart(DEFAULT_API_URL)
            .file(getMultipartFile("cat.png", "file"))

        this.mockMvc
            .perform(builder)
            .andExpect(status().isOk)
            .andExpect(jsonPath("fileName").value("cat.png"))
            .andDo(print())
    }

    @Test
    fun `다중 파일 등록 테스트`() {
        val builder: MockHttpServletRequestBuilder = MockMvcRequestBuilders.multipart("$DEFAULT_API_URL/mutiple")
            .file(getMultipartFile("cat.png", "files"))
            .file(getMultipartFile("lion2.jpeg","files"))

        this.mockMvc
            .perform(builder)
            .andExpect(status().isOk)
            .andExpect(jsonPath("[0].fileName").value("cat.png"))
            .andExpect(jsonPath("[1].fileName").value("lion2.jpeg"))
            .andDo(print())
    }

    @Test
    @Ignore
    fun `파일 다운로드 테스트`() {
        this.mockMvc
            .perform(get("$DEFAULT_API_URL/download")
                .param("path","contents/lion.jpeg")
                .param("fileName", "lion.jpeg"))
            .andExpect(status().isOk)
    }

    @Test
    fun `파일 조회 테스트`() {
        this.mockMvc
            .perform(get(DEFAULT_API_URL)
                .param("path","contents"))
            .andExpect(status().isOk)
            .andDo(print())
    }

    @Test
    @Ignore
    fun `파일 이동 테스트`() {
        this.mockMvc
            .perform(patch(DEFAULT_API_URL)
                .param("path","contents/lion2.jpeg")
                .param("movePath","contents/move/lion2.jpeg"))
            .andExpect(status().isOk)
            .andDo(print())
    }

    @Test
    @Ignore
    fun `단일 파일 삭제 테스트`() {
        this.mockMvc
            .perform(delete(DEFAULT_API_URL)
                .param("path","contents/move/lion2.jpeg"))
            .andExpect(status().isOk)
    }

    @Test
    @Ignore
    fun `다중 파일 삭제 테스트`() {
        this.mockMvc
            .perform(delete("$DEFAULT_API_URL/list")
                .param("fileList","contents/cat.png,contents/lion2.jpeg"))
            .andExpect(status().isOk)
    }

    @AfterEach
    fun shutdownMockS3() {
        s3Mock.stop()
    }
}