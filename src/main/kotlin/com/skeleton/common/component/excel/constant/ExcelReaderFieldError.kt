package com.skeleton.common.component.excel.constant

/**
 * Created by KMS on 2021/05/25.
 */
enum class ExcelReaderFieldError(var message: String) {
    TYPE("잘못된 데이터 타입: "),
    EMPTY("필수 입력값 누락"),
    VALID("유효성 검증 실패"),
    UNKNOWN("알수 없는 에러")
    ;

    companion object {
        var messageToMap: MutableMap<String, ExcelReaderFieldError> = mutableMapOf()

        /**
         * 취미명으로 취미 맵으로 맵핑
         * @param code
         * @return
         */
        fun getExcelReaderErrorConstant(name: String): ExcelReaderFieldError? {
            if(messageToMap.isEmpty())
                initMapping()
            return messageToMap[name]
        }

        /**
         * 맵 초기화
         */
        private fun initMapping() {
            messageToMap = mutableMapOf()
            values().forEach {
                messageToMap[it.name] = it
            }
        }
    }
}