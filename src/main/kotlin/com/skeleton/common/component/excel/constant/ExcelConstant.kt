package com.skeleton.common.component.excel.constant

/**
 * 엑셀 공통 기능 상수
 * Created by KMS on 2021/05/25.
 */
class ExcelConstant {

    companion object {
        val FILE_NAME: String = "fileName"
        val TITLE: String = "title"
        val HEADER: String = "header"
        val BODY: String = "body"

        val COLOMN_WIDTH: String = "colomnWidth"
        val STYLE: String = "style"
        val AUTO_SIZING: String = "autoSizing"

        val XLS: String = "xls"
        val XLSX: String = "xlsx"
        val XLSX_STREAM: String = "xlsx-stream"

        /* 기본 컬럼 넓이 확장 사이즈 */
        val DEFAULT_COLOUMN_WIDTH: Int = 3000

        /* 엑셀 다운로드 상수 */ /* XLSX-STREAMING */
        val EXCEL_XLSX_STREAMING_VIEW: String = "excelXlsxStreamingView"

        /* XLSX */
        val EXCEL_XLSX_VIEW: String = "excelXlsxView"

        /* XLS */
        val EXCEL_XLS_VIEW: String = "excelXlsView"
    }

}