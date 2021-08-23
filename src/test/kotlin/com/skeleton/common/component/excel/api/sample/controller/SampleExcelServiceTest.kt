package com.skeleton.common.component.excel.api.sample.controller

import com.skeleton.common.BaseMvcTest
import com.skeleton.common.component.excel.api.sample.domain.SampleExcelDownload
import com.skeleton.common.component.excel.domain.dto.ReqExcelDownload
import com.skeleton.common.component.excel.service.ExcelService
import org.junit.jupiter.api.Test

/**
 * Created by KMS on 2021/08/12.
 */
class SampleExcelServiceTest(
    val excelService: ExcelService
): BaseMvcTest() {

    @Test
    fun `엑셀 다운로드 테스트`() {

        excelService.download(listOf<SampleExcelDownload>(), ReqExcelDownload())
    }

}