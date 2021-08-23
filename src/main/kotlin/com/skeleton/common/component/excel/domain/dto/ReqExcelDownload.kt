package com.skeleton.common.component.excel.domain.dto

/**
 * 엑셀 다운로드 확장 도메인
 * Created by KMS on 2021/05/25.
 */
class ReqExcelDownload (
    /* 엑셀 제목 */
    var title: String? = null,
    /* 엑셀 Header */
    var header: Array<String> = arrayOf(),
    /* 엑셀 fileName */
    var fileName: String? = null,
    /* 엑셀 컬럼 사이즈(지정하지 않으면 기본 사이즈(3000)가 지정된다) */
    var columnWidth: Int? = null,
    /* 스타일 여부 기본 false */
    var style: Boolean = false,
    /* 오토 컬럼 리사이징 사용 여부 기본 false */
    var autoSize: Boolean = false
) {
    override fun toString(): String {
        return "ReqExcelDownload(title=$title, header=${header.contentToString()}, fileName=$fileName, columnWidth=$columnWidth, style=$style, autoSize=$autoSize)"
    }
}