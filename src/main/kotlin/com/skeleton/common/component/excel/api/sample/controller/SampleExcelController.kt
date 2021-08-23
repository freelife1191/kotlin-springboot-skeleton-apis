package com.skeleton.common.component.excel.api.sample.controller

import com.skeleton.common.component.excel.api.sample.service.SampleExcelService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

/**
 * Created by KMS on 2021/07/30.
 */
@RestController
@RequestMapping("/tutorial/excel")
@Tag(name = "[Sample] 공통 Excel", description = "SampleExcelController")
class SampleExcelController(
    val sampleExcelService: SampleExcelService) {

    @Operation(summary = "Sample 엑셀 양식 다운로드")
    @GetMapping("/form")
    @Throws(IOException::class)
    fun getSampleExcelForm(): ResponseEntity<Resource> = sampleExcelService.getSampleExcelForm()

    @Operation(summary = "Sample 엑셀 업로드", description = "# Sample 엑셀 업로드를 테스트 해볼 수 있는 API\n" +
            "## 엑셀 업로드 공통 서비스 가이드\n" +
            "---\n" +
            "https://parkingcloud.dooray.com/project/2525192394467198586?contentsType=wiki&pageId=2697094616920924080\n\n" +
            "## 사용예시\n" +
            "---\n" +
            "`val dataList: List<SampleExcelDownload>  = ExcelReader.getObjectList(file) { ExcelUtils.from(it) }`")
    @ApiResponses(
            ApiResponse(responseCode = "4010", description = "읽을 수 없는 엑셀 파일 입니다 (DRM 적용 또는 다른이유)"),
            ApiResponse(responseCode = "4011", description = "엑셀 업로드 FIELD ERROR :: 입력 데이터를 확인하세요\n" +
                    "## Sample Data\n" +
                    "### TYPE\n" +
                    "___\n" +
                    "`TYPE`: 잘못된 데이터 타입\n" +
                    "`EMPTY`: 필수 입력값 누락\n" +
                    "`VALID`: 유효성 검증 실패\n" +
                    "`UNKNOWN`: 알수 없는 에러\n" +
                    "```json" +
                    "" +
                    "'[\n" +
                    "[\n"+
                    "  {\n" +
                    "    \"type\": \"EMPTY\",\n" +
                    "    \"row\": 2,\n" +
                    "    \"field\": \"name\",\n" +
                    "    \"fieldHeader\": \"이름\",\n" +
                    "    \"inputData\": \"\",\n" +
                    "    \"message\": \"필수 입력값 누락\",\n" +
                    "    \"exceptionMessage\": \"이름은 필수 입력값입니다\"\n" +
                    "  }\n" +
                    "]"+
                    "")
    )
    @PostMapping
    @Throws(Exception::class)
    fun insertEquipment(
        @Parameter(description = "업로드 엑셀파일 - 업무에 해당하는 엑셀 업로드 양식을 다운 받아 양식에 맞게 작성한뒤 업로드해야함")
        @RequestPart(value = "file") file: MultipartFile) = sampleExcelService.excelUpload(file)

    @Operation(summary = "Sample 엑셀 다운로드")
    @GetMapping
    @Throws(IOException::class)
    fun getSampleExcel() = sampleExcelService.getSampleExcel()
}