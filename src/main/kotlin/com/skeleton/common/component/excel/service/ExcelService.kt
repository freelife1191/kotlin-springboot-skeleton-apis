package com.skeleton.common.component.excel.service

import com.skeleton.common.component.excel.constant.ExcelConstant.Companion.EXCEL_XLSX_STREAMING_VIEW
import com.skeleton.common.component.excel.domain.dto.ReqExcelDownload
import org.springframework.stereotype.Service
import org.springframework.web.servlet.ModelAndView

/**
 * Created by KMS on 2021/05/25.
 */
@Service
class ExcelService(var excelConverter: ExcelConverter) {

    /**
     * 엑셀 다운로드 서비스
     * Map 이나 객체 형태로 받아서 엑셀 파일로 만들어 리턴함
     * 엑셀 다운로드 서비스를 이용하기 위해서 요청 파라메터는
     * ReqExcel 엑셀 객체를 상속 받아서 파라메터로 전달해야함
     * @param list
     * @param <T>
     * @return
    </T> */
    fun <T> download(list: MutableList<T>): ModelAndView {
        return ModelAndView(EXCEL_XLSX_STREAMING_VIEW, excelConverter.convertList(list))
    }

    /**
     * 엑셀 다운로드 서비스
     * Map 이나 객체 형태로 받아서 엑셀 파일로 만들어 리턴함
     * 엑셀 다운로드 서비스를 이용하기 위해서 요청 파라메터는
     * ReqExcel 엑셀 객체를 상속 받아서 파라메터로 전달해야함
     * @param list
     * @param <T> 엑셀 변환 데이터
     * @param <E extends ReqExcel> 엑셀 파라메터
     * @return
    </E></T> */
    fun <T> download(list: List<T>, req: ReqExcelDownload): ModelAndView {
        return ModelAndView(EXCEL_XLSX_STREAMING_VIEW, excelConverter.convertList(list, req))
    }
}