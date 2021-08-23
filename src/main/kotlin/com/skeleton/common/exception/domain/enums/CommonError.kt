package com.skeleton.common.exception.domain.enums

import org.springframework.http.HttpStatus

/**
 * ErrorCode 응답 오류 코드 정의
 * https://jsonobject.tistory.com/501
 *
 * 애플리케이션에서 발생하는 다양한 예외를 맵핑하여 클라이언트가 노출할 오류 코드를 정의하는 단계이다
 *
 * 아래 3개는 가장 일반적은 공통 오류 코드를 정의한 것이다. 애플리케이션의 비지니스 로직에 따라 발생하는 다양한 오류 코드를 추가로 정의하면 된다
 * Created by KMS on 2021/05/08.
 */
enum class ErrorCode(val code: Int, val message: String) {
    //2XX
    DATA_NOT_FOUND(HttpStatus.NO_CONTENT.value(),"일치하는 데이터가 없습니다"),
    //4XX
    ERROR_REQUEST(HttpStatus.BAD_REQUEST.value(),"잘못된 요청 :: 요청 정보를 확인하세요"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "권한없음"),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),"지원하지 않는 HTTP 메서드입니다"),
    //5XX
    ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(),"서버에러가 발생했습니다. 서버 관리자에게 문의하세요"),
    NETWORK_ERROR(550, "NetWork Error"),
    REST_API_ERROR(551, "REST API 통신 에러"),

    //6XX
    ERROR_INVALID_PARAM(600,"파라메터 유효성 검증 실패"),
    INVALID_HTTP_REQUEST(601,"HTTP 요청 에러"),

    //7XX
    FEIGN_CLIENT_DECORD_ERROR(701, "Feign Client Decord Error"),
    DECRYPTION_ERROR(702, "복호화에 실패하였습니다."),

    RESULT_NULL(800, "Result Null"),
    FAIL(900, "Fail"),

    EXCEL_ERROR(1000,"Excel Error"),
    EXCEL_READER_FIELD_ERROR(1001,"Excel Reader Field Error"),
    EXCEL_READER_FILE_ERROR(1002,"Excel Reader File Error"),
    EXCEL_READER_ERROR(1003,"Excel Reader Error"),

    FILE_REQUEST_ERROR(2000, "File Request Error"),
    FILE_NOT_EXIST(2001, "File Not Exist"),
    FILE_NOT_MAKE_PATH(2002, "File Not Make Path"),
    FILE_UPLOAD_FAILED(2003, "File Upload Failed"),
    FILE_TEMP_APPLY_FAILED(2004, "File Temp Apply Failed"),
    FILE_DOWNLOAD_FAILED(2005, "File Download Failed"),
    FILE_DELETE_FAILED(2006, "File Delete Failed"),
    FILE_ACCESS_DENIED(2007, "File Access Denied"),
    FILE_PROCESS_FAILED(2008, "File Arocess Failed"),

    /* File Error Message */
    FILE_REQUEST_MISSING_PART(2009, "File Request Missing Part"),
    FILE_DUPLICATE(2010, "File Duplicate"),
    FILE_MAX_UPLOAD_SIZE_EXCEEDED(2011, "파일 업로드 사이즈 초과"),

    DATA_DUPLICATE(3000, "Data Duplicate"),
    DATA_REGISTRATION_FAIL(3001, "Data Registration Fail"),
    EXCEED_MAXIMUM_DATA(3002, "Exceed Maximum Data"),
    DATA_OVERWRITE(3003, "Data Overwrite"),
    ;

    companion object {

        var codeToMap: MutableMap<Int, ErrorCode> = mutableMapOf()

        /**
         * 코드 값으로 ResCode 맵으로 맵핑
         * @param code
         * @return
         */
        fun getErrorCode(code: Int): ErrorCode? {
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