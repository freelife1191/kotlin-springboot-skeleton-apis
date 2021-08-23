package com.skeleton.common.exception.domain.enums

import com.skeleton.common.exception.domain.enums.ErrorCode.*
import org.slf4j.event.Level
import java.lang.Exception

/**
 * Exception 타입에 따른 메세지 처리 상수
 * Created by KMS on 2021/05/14.
 */
enum class CommonError(
    /* 응답코드객체 */
    val errorCode: ErrorCode,
    /* 응답메세지 */
    val message: String,
    /* e.getMessage true:추가, false:미추가 */
    val exception: Boolean,
    /* Log Level */
    var level: Level) {

    /** 기본 서버 에러 **/
    HttpServerErrorException(ERROR, "", true, Level.ERROR),

    /** HTTP 요청 에러 **/
    HttpMessageNotReadableException(INVALID_HTTP_REQUEST, "HTTP 요청 에러 :: ", false,  Level.ERROR),
    /** 지원되지 않는 HTTP METHOD 에러 핸들러 **/
    HttpRequestMethodNotSupportedException(UNSUPPORTED_MEDIA_TYPE, "지원되지 않는 HTTP METHOD :: ", false, Level.ERROR),

    /** 데이터 등록 실패 에러핸들러 **/
    DataRegistrationFailedException(DATA_NOT_FOUND, DATA_REGISTRATION_FAIL.message+": 데이터 등록이 실패했습니다 :: ",true, Level.ERROR),
    /** 파일 적용 시 BODY가 없을 때 **/
    DataNotFoundException(DATA_NOT_FOUND, "Data not found: 요청하신 데이터를 찾을 수 없습니다 :: ", true, Level.ERROR),
    /** 중복된 데이터 처리 에러 핸들러 **/
    DuplicateDataException(DATA_DUPLICATE, DATA_DUPLICATE.message+": ", true, Level.ERROR),
    /** 데이터베이스 중복 키 에러 **/
    DuplicateKeyException(DATA_DUPLICATE, "중복된 키가 있어 데이터베이스 등록에 실패 했습니다 :: ", true, Level.ERROR),

    /** RestTemplate API 통신 에러 핸들러 **/
    ResourceAccessException(ERROR, "REST API 통신 에러 :: ", true, Level.ERROR),
    /** RestTemplate API 통신 에러 핸들러 **/
    RestClientException(ERROR, "REST API 통신 에러 :: ", true, Level.ERROR),

    /** 파일이 용량 제한 에러 **/
    MaxUploadSizeExceededException(FILE_MAX_UPLOAD_SIZE_EXCEEDED, "", false, Level.WARN), //최대 100MB까지 등록할 수 있습니다.
    /** 파일이 용량 제한 에러 **/
    SizeLimitExceededException(FILE_MAX_UPLOAD_SIZE_EXCEEDED, "", false, Level.WARN), //최대 100MB까지 등록할 수 있습니다.
    /** MultiPart 파일 전송 누락 에러 **/
    MissingServletRequestPartException(FILE_REQUEST_MISSING_PART, "잘못된 전송 파일명 또는 전송 파일 누락 에러 :: ", true, Level.ERROR),
    /** MultiPart 파일 전송 요청 에러 **/
    MultipartException(FILE_REQUEST_ERROR, "파일 전송 요청 에러 :: ", true, Level.ERROR),

    /** 프로세스를 더이상 진행할 수 없을 때 **/
    UnprocessableException(ERROR, "", true, Level.ERROR),
    /** 지원되지 않는 기능에 대한 예외처리 **/
    NotSupportedException(ERROR, "", true, Level.ERROR),

    /** JWT 토큰 복호화 ERROR 핸들러 **/
    InvalidSignatureException(ERROR_REQUEST, "JWT 토큰 복호화 ERROR :: ", true, Level.ERROR),
    /** 복호화 실패 처리 **/
    DecryptionException(DECRYPTION_ERROR, "복호화에 실패하였습니다.", true, Level.WARN),

    /** 파라메터 유효성 체크 실패 에러 핸들러 **/
    ParameterValidationFailedException(ERROR_INVALID_PARAM, "파라메터 유효성 검증 실패 :: ", true, Level.WARN),
    /** 사용자 정의 필수 파라메터 누락 에러 핸들러 **/
    RequestParamRequiredException(ERROR_INVALID_PARAM, "필수 파라메터 누락 :: ", true, Level.WARN),
    /** Parameter Validation Error **/
    MissingServletRequestParameterException(ERROR_INVALID_PARAM, "", true, Level.ERROR),
    /** Parameter Validation Error **/
    BindException(ERROR_INVALID_PARAM, "파라메터 유효성 검증 실패 ::", false, Level.ERROR),
    /** Parameter Validation Error **/
    MethodArgumentNotValidException(ERROR_INVALID_PARAM, "파라메터 유효성 검증 실패 ::", false, Level.ERROR),

    /** JSON 파싱 에러 **/
    JsonMappingException(ERROR_REQUEST, "JSON 파싱 ERROR :: ", true, Level.ERROR),
    /** Header 필수값 누락 에러 핸들러 **/
    MissingRequestHeaderException(ERROR_INVALID_PARAM, "Header 필수값 누락 ERROR: JWT 토큰은 필수 입력값 입니다 :: ", false, Level.ERROR),
    /** 맵핑 에러 **/
    IllegalStateException(ERROR_REQUEST, "", true, Level.ERROR),
    /** 인자값 형식 에러 **/
    IllegalArgumentException(ERROR_INVALID_PARAM, "", false, Level.ERROR),
    /** Kotlin non-null 파라메터 예외 처리 **/
    BeanInstantiationException(ERROR, "Kotlin non-null 파라메터를 확인하세요 :: ", true, Level.ERROR),
    /** JWT 토큰 인증정보 확인 예외 처리 **/
    AuthenticationException(UNAUTHORIZED, "[AccessDenied] Header의 [Authorization] 인증정보를 확인하세요 :: ", true, Level.ERROR),
    /** FeignClient 통신 에러 */
    DecodeException(FEIGN_CLIENT_DECORD_ERROR, "FaignClient 응답데이터 형식에 문제가 있어 Json Parsing에 실패했습니다 :: ", true, Level.ERROR),
    /** JWT 토큰 예외 처리 **/
    JwtException(UNAUTHORIZED, "잘못된 토큰입니다.", true, Level.WARN),
    /** 잘못된 파라메터 타입 */
    MethodArgumentTypeMismatchException(ERROR_REQUEST, "잘못된 파라메터 타입 ::", false, Level.ERROR),
    /** 입출력 예외 처리 **/
    IOException(ERROR, "", true, Level.ERROR),
    /** 입출력 예외 처리 **/
    ClientAbortException(ERROR, "", true, Level.ERROR),

    /** 엑셀 데이터 처리중 에러 **/
    ExcelComponentException(ERROR, "엑셀 데이터 처리중 ERROR: ", true, Level.ERROR),
    /** 엑셀 업로드 Field 에러 **/
    ExcelReaderFieldException(EXCEL_READER_FIELD_ERROR, "엑셀 업로드 FIELD ERROR :: 입력 데이터를 확인하세요 :: ", false, Level.WARN),
    /** 엑셀 업로드시 읽을 수 없는 엑셀 파일 에러 **/
    ExcelReaderFileException(EXCEL_READER_FILE_ERROR, "읽을 수 없는 엑셀 파일 입니다 (DRM 적용 또는 다른이유) :: ", false, Level.ERROR),
    /** 엑셀 업로드시 엑셀 파일이 아닌 확장자 파일에 대한 에러 **/
    ExcelReaderFileExtentionException(EXCEL_READER_FILE_ERROR, "엑셀 업로드는 [xlsx, xls] 확장자 파일만 처리가 가능합니다 :: ", false, Level.ERROR),
    /** 엑셀 업로드 시 캐치 하지 못한 그외 에러 **/
    ExcelReaderException(EXCEL_READER_ERROR, "엑셀 업로드 ERROR :: ", true, Level.ERROR),

    /** 파일이 존재하지 않을 때 **/
    FileNotExistException(FILE_NOT_EXIST, "", true, Level.ERROR),
    /** 파일 등록 필수 파라메터 체크 **/
    FileRequestFileNotException(ERROR_INVALID_PARAM, "", true, Level.ERROR),
    /** 파일 덮어쓰기 여부 확인 **/
    FileDuplicateException(ERROR_REQUEST, "", true, Level.ERROR),
    /** 파일 등록 필수 파라메터 체크 **/
    FileRequestParamRequiredException(ERROR_INVALID_PARAM, "파일 업로드 필수 파라메터 누락 :: ", true, Level.ERROR),
    /** 파일 업로드 실패 에러 핸들러 **/
    FileUploadFailException(FILE_UPLOAD_FAILED, "파일 업로드 실패: 잠시 후 다시 시도 해주세요 :: ", false, Level.ERROR),
    /** 파일 다운로드 실패 에러 핸들러 */
    FileDownloadFailException(FILE_DOWNLOAD_FAILED, "파일 다운로드 실패: 존재 하지 않는 파일은 자동 삭제처리 됩니다 :: ", true, Level.ERROR),
    /** 파일 삭제 실패 에러 핸들러 */
    FileDeleteFailException(FILE_DELETE_FAILED, "파일 삭제 실패: 삭제중 오류가 발생했습니다 :: ", true, Level.ERROR),
    /** TEMP 파일 적용 실패 */
    FileTempApplyFailException(FILE_TEMP_APPLY_FAILED, "TEMP 파일 적용 실패: 임시 파일 적용 정보를 확인하세요 :: ", true, Level.ERROR),
    /** 파일 경로 생성 실패 */
    FileNotMapePathException(FILE_NOT_MAKE_PATH, "파일 경로 생성 실패: 관리자에게 문의 하세요 :: ", true, Level.ERROR),
    /** AWS 권한 에러 */
    FileAwsS3AccessDeniedException(FILE_ACCESS_DENIED, "AWS S3 권한 에러: 파일 처리 실패 :: ", true, Level.ERROR),
    /** AWS S3 파일 처리 실패 */
    FileAwsS3ProcessException(FILE_PROCESS_FAILED, "AWS S3 파일 처리 에러: 파일 처리 실패 :: ", true, Level.ERROR),
    ;

    companion object {

        var nameToMap: MutableMap<String, CommonError> = mutableMapOf()

        /**
         * 코드 값으로 ResCode 맵으로 맵핑
         * @param code
         * @return
         */
        fun getCommonException(e: Exception): CommonError {
            if(nameToMap.isEmpty())
                initMapping()
            return nameToMap[e::class.java.simpleName]
                ?: HttpServerErrorException
        }

        /**
         * 맵 초기화
         */
        private fun initMapping() {
            nameToMap = mutableMapOf()
            values().forEach {
                nameToMap[it.name] = it
            }
        }

    }

}