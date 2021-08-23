package com.skeleton.common.component.excel.domain.model

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 엑셀 업로드 에러 필드
 * Sample
[
  {
    "errorType": "TYPE",
    "errorRow": 2,
    "errorFieldName": "test",
    "errorFieldHeaderName": "테스트",
    "errorInputData": "안녕",
    "errorMessage": "잘못된 데이터 타입: 데이터 필드타입 - Integer, 입력된 필드타입 - String",
    "exceptionMessage": "NumberFormatException: For input string: \"안녕\""
  }
]
 * Created by KMS on 2021/05/25.
 */
@Schema(title = "엑셀 업로드 에러 필드 객체", hidden = true)
class ExcelReaderErrorField(
    @field:Schema(title = "ERROR 타입", description = "TYPE: 잘못된 데이터 타입, EMPTY: 필수 입력값 누락, VALID: 유효성 검증 실패, UNKNOWN: 알수 없는 에러")
    var type: String? = null,
    @field:Schema(title = "ERROR 행 번호")
    var row: Int? = null,
    @field:Schema(title = "ERROR 필드명")
    var field: String? = null,
    @field:Schema(title = "ERROR 필드 Header명")
    var fieldHeader: String? = null,
    @field:Schema(title = "ERROR 입력값")
    var inputData: String? = null,
    @field:Schema(title = "ERROR 메세지")
    var message: String? = null,
    @field:Schema(title = "EXCEPTION MESSAGE")
    var exceptionMessage: String? = null
) {
    override fun toString(): String {
        return "ExcelReaderErrorField(type=$type, row=$row, field=$field, fieldHeader=$fieldHeader, inputData=$inputData, message=$message, exceptionMessage=$exceptionMessage)"
    }
}