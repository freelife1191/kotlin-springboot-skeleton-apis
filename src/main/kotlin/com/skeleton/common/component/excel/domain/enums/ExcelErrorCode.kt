package com.skeleton.common.component.excel.domain.enums

/**
 * ErrorCode 응답 오류 코드 정의
 * https://jsonobject.tistory.com/501
 *
 * 애플리케이션에서 발생하는 다양한 예외를 맵핑하여 클라이언트가 노출할 오류 코드를 정의하는 단계이다
 *
 * 아래 3개는 가장 일반적은 공통 오류 코드를 정의한 것이다. 애플리케이션의 비지니스 로직에 따라 발생하는 다양한 오류 코드를 추가로 정의하면 된다
 * Created by KMS on 2021/05/08.
 */
enum class ExcelErrorCode(val code: Int, val message: String) {

    EXCEL_ERROR(1000,""),
    EXCEL_READER_FIELD_ERROR(1001,""),
    EXCEL_READER_FILE_ERROR(1002,""),
    EXCEL_READER_ERROR(1003,"");

    companion object {

        var codeToMap: MutableMap<Int, ExcelErrorCode> = mutableMapOf()

        /**
         * 코드 값으로 ResCode 맵으로 맵핑
         * @param code
         * @return
         */
        fun getErrorCode(code: Int): ExcelErrorCode? {
            if(codeToMap.isEmpty())
                initMapping()
            return codeToMap[code]
        }

        /**
         * 맵 초기화
         */
        private fun initMapping() {
            codeToMap = mutableMapOf()
            values().forEach {
                codeToMap[it.code] = it
            }
        }

    }

}