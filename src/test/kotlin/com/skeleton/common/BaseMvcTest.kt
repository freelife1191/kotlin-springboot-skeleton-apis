package com.skeleton.common

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter
import org.springframework.web.filter.OncePerRequestFilter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by LYT to 2021/04/06
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BaseMvcTest {

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var ctx: WebApplicationContext

    @Autowired
    lateinit var snakeCaseConverterFilter: OncePerRequestFilter

    fun applyEncodingFilter() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.ctx)
            .addFilters<DefaultMockMvcBuilder>(
                CharacterEncodingFilter("UTF-8", true),
                snakeCaseConverterFilter
            )
            .alwaysDo<DefaultMockMvcBuilder>(print())
            .build()
    }

    /**
     * 파일 업로드 데이터 생성
     * @param originalFileName 원본파일명
     * @param reqFileName 요청파일명(API에서 받는 이름)
     * @return
     */
    fun getMultipartFile(originalFileName: String?, reqFileName: String?): MockMultipartFile {
        return getMultipartFile(null, originalFileName, reqFileName)
    }

    /**
     * 파일 업로드 데이터 생성
     * @param originalFileName 원본파일명
     * @param reqFileName 요청파일명(API에서 받는 이름)
     * @return
     */
    fun getMultipartFile(
        fileMiddlePath: String?,
        originalFileName: String?,
        reqFileName: String?
    ): MockMultipartFile {
        var filePath = Paths.get("src", "test", "resources", "file")
        if (StringUtils.isNotEmpty(fileMiddlePath)) filePath = Path.of(filePath.toString(), fileMiddlePath)
        filePath = Paths.get(filePath.toString(), originalFileName)
        // File file = new File(String.valueOf(filePath));

        //        String contentType = "";
        //        String contentType = "image/png";
        //        String contentType = "application/zip";
        //            File file = new File(String.valueOf(path));
        return MockMultipartFile(reqFileName!!, originalFileName, null, Files.readAllBytes(filePath))
    }
}
