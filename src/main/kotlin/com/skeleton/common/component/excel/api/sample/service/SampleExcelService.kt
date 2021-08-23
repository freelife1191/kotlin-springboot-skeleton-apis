package com.skeleton.common.component.excel.api.sample.service

import com.skeleton.common.component.excel.api.sample.domain.SampleExcelDownload
import com.skeleton.common.component.excel.api.sample.domain.SampleExcelUpload
import com.skeleton.common.domain.response.CommonResponse
import com.skeleton.common.component.excel.domain.dto.ReqExcelDownload
import com.skeleton.common.component.excel.service.ExcelReader
import com.skeleton.common.component.excel.service.ExcelService
import com.skeleton.common.component.excel.utils.ExcelUtils
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import java.io.File
import java.io.IOException
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by KMS on 2021/07/30.
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@Service
class SampleExcelService(
    val excelService: ExcelService
) {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Sample 엑셀 업로드 테스트
     * @param file
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun excelUpload(file: MultipartFile): CommonResponse<List<SampleExcelUpload>> {
        val dataList: List<SampleExcelUpload> = ExcelReader.getObjectList(file) { ExcelUtils.from(it) }
        for (dto in dataList) {
            log.info("Excel Upload Sample Data = {}", dto)
        }
        return CommonResponse(dataList)
    }

    /**
     * Sample 엑셀 양식 다운로드
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getSampleExcelForm(): ResponseEntity<Resource> {
        val fileName = "sampleUpload.xlsx"

        // 리소스 파일 획득하기
        // /src/main/resources/something.txt 파일을 읽어 온다.
        // String something = IOUtils.toString(getClass().getResourceAsStream("/something.txt"), "UTF-8");

        //Path filePath = Paths.get("src", "resources", "file", File.separatorChar + fileName);
        // Resource resource = resourceLoader.getResource(filePath.toString());
        val filePath = Paths.get("${File.separatorChar}file", fileName)
        val resource: Resource =  InputStreamResource(javaClass.getResourceAsStream(filePath.toString()))
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .cacheControl(CacheControl.noCache())
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName")
            .body(resource)
    }

    /**
     * Sample 엑셀 다운로드
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getSampleExcel(): ModelAndView {
        val fileName = "sampleUpload.xlsx"

        // 리소스 파일 획득하기
        // /src/main/resources/something.txt 파일을 읽어 온다.
        // String something = IOUtils.toString(getClass().getResourceAsStream("/something.txt"), "UTF-8");

        //Path filePath = Paths.get("src", "resources", "file", File.separatorChar + fileName);
        // Resource resource = resourceLoader.getResource(filePath.toString());
        val filePath = Paths.get("${File.separatorChar}file", fileName)

        val file = File(fileName)
        FileUtils.copyInputStreamToFile(javaClass.getResourceAsStream(filePath.toString()), file)
        log.debug("## filePath = $filePath, file.length = ${file.length()}")

        val dataList: List<SampleExcelDownload>  = ExcelReader.getObjectList(file) { ExcelUtils.from(it) }

        log.debug("## dataList = $dataList")
        //dataList.forEach { log.debug("## data = $it") }

        return excelService.download(dataList,
            ReqExcelDownload(
                header = arrayOf("이름", "이메일", "전화번호", "소속부서", "업무코드", "부서코드", "내용", "IP", "소수", "날짜", "일시", "빈데이터"),
                fileName = "샘플파일_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}"
            )
        )
    }

}