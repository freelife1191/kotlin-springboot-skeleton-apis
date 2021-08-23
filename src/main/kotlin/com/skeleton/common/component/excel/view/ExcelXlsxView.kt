package com.skeleton.common.component.excel.view

import com.skeleton.common.component.excel.service.ExcelWriter
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.stereotype.Component
import org.springframework.web.servlet.view.document.AbstractXlsxView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by KMS on 2021/05/25.
 */
@Component
class ExcelXlsxView: AbstractXlsxView() {
    override fun buildExcelDocument(
        model: MutableMap<String, Any>,
        workbook: Workbook,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        ExcelWriter(workbook, model, request, response).create()
    }
}