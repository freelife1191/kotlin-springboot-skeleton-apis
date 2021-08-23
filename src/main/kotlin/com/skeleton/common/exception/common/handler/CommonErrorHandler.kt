package com.skeleton.common.exception.common.handler

import com.fasterxml.jackson.databind.JsonMappingException
import com.skeleton.common.component.excel.exception.*
import com.skeleton.common.domain.response.CommonResponse
import com.skeleton.common.exception.DataNotFoundException
import com.skeleton.common.exception.domain.enums.CommonError
import com.skeleton.common.exception.utils.ErrorUtils
import com.skeleton.common.utils.JsonUtils
import org.apache.commons.fileupload.FileUploadBase
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.http.auth.AuthenticationException
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import org.springframework.beans.BeanInstantiationException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.MaxUploadSizeExceededException
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.LinkedHashMap

/**
 * 공통 Controller Exception Handler
 * Created by KMS on 2021/04/18.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class CommonErrorHandler {//: ResponseEntityExceptionHandler() {
val log: org.slf4j.Logger = LoggerFactory.getLogger(this::class.java)

    private val CLASS_NAME = "Common"

    @ExceptionHandler(value = [
        // HTTP 요청 에러
        HttpMessageNotReadableException::class,
        // 지원되지 않는 HTTP METHOD 에러 핸들러
        HttpRequestMethodNotSupportedException::class,
        // 임시 파일 적용 시 BODY가 없을 때
        DataNotFoundException::class,
        // 파일이 용량 제한 에러
        MaxUploadSizeExceededException::class,

        // Parameter Validation Error
        BindException::class,
        // Parameter Validation Error
        MethodArgumentNotValidException::class,

        // 인자값 형식 에러
        IllegalArgumentException::class,
        // Kotlin non-null 파라메터 예외 처리
        BeanInstantiationException::class,
        // JWT 토큰 인증정보 확인 예외 처리
        AuthenticationException::class,
        // FeignClient 통신 에러
        javax.websocket.DecodeException::class,
        // 잘못된 파라메터 타입
        MethodArgumentTypeMismatchException::class,
        // 입출력 예외 처리
        IOException::class,

        // 엑셀 데이터 처리중 에러
        ExcelComponentException::class,
        // 엑셀 업로드 필드 에러 핸들러
        ExcelReaderFieldException::class,
        // 엑셀 업로드 파일 에러 핸들러
        ExcelReaderFileException::class,
        // 엑셀 업로드 파일 확장자 에러 핸들러
        ExcelReaderFileExtentionException::class,
        // 엑셀 업로드 시 캐치 하지 못한 그외 에러
        ExcelReaderException::class,
    ])
    @Throws(Exception::class)
    fun commonExcetpion(e: Exception, request: HttpServletRequest, webRequest: WebRequest): CommonResponse<Any> {

        // 공통 Exception 객체 셋팅
        val error = CommonError.getCommonException(e)
        // log.debug("## error = ${e::class.java.simpleName}")
        // log.debug("## error = ${e::class.qualifiedName}")
        // log.debug("## error = ${e::class.java.name}")
        // log.debug("## error = ${e::class.java.canonicalName}")

        var ERROR_TITLE =
            if(error.exception) error.message + ExceptionUtils.getRootCauseMessage(e)
            else error.message.ifEmpty { ExceptionUtils.getRootCauseMessage(e) ?: "Empty Error Message" }

        // else ExceptionUtils.getRootCauseMessage(e) ?: "Empty Error Message" //error.message.ifEmpty {  }

        val errorMap: Map<String, Any> = ErrorUtils.setErrorMap(request, webRequest)

        var commonResponse: CommonResponse<Any>? = null
        var CUSTOM_MSG: String? = null //커스텀 Exception Message

        /* Exception 별 커스텀 처리 */
        when (error) {
            CommonError.MaxUploadSizeExceededException -> {
                var permittedSize = 0L //제한용량
                var actualSize = 0L //요청 파일용량
                val me = e as MaxUploadSizeExceededException
                me.cause?.run {
                    if (this is FileUploadBase.SizeLimitExceededException) {
                        permittedSize = if (this.permittedSize != 0L) this.permittedSize / 1024 / 1024 else 0L
                        actualSize = if (this.actualSize != 0L) this.actualSize / 1024 / 1024 else 0L
                        CUSTOM_MSG = "최대 업로드 파일용량 제한초과 :: 제한용량 = ${permittedSize}MB, 요청 파일용량 = ${actualSize}MB"
                    }
                    ERROR_TITLE += "최대 ${permittedSize}MB까지 등록할 수 있습니다: 요청 파일용량 = ${actualSize}MB"
                }
            }
            CommonError.BindException -> {
                ERROR_TITLE += getBindResultFieldErrorMessage((e as BindException).bindingResult)
            }
            CommonError.MethodArgumentNotValidException -> ERROR_TITLE +=
                getBindResultFieldErrorMessage((e as MethodArgumentNotValidException).bindingResult)
            /*
            CommonException.ExcelReaderFieldException -> {
                commonResponse = CommonResponse(error.errorCode.code, ERROR_MSG, ExcelReader.errorFieldList)
                errorMap["CommonResult"] = commonResponse
            }
            */
            CommonError.HttpMessageNotReadableException -> {
                if (e.message?.startsWith("Required request body is missing") == true)
                    ERROR_TITLE += "HTTP Request Body 누락 :: HTTP Request Body 는 필수 입력값 입니다"
                else {
                    val hm = e as HttpMessageNotReadableException
                    hm.cause?.run {
                        if (this is JsonMappingException) {
                            ERROR_TITLE += "JSON 파싱 ERROR: JSON 형식에 문제가 있어 Parsing에 실패 했습니다 :: ${this.message}"
                            CUSTOM_MSG = this.message
                        }
                    }
                }
            }
            CommonError.IllegalArgumentException -> {
                val illegalArgumentExceptionMessage = "JWT must have 3 tokens"
                if (e.message!!.contains(illegalArgumentExceptionMessage))
                    ERROR_TITLE += "JWT 데이터 형식 ERROR: JWT에는 3 개의 토큰이 있어야 합니다\n입력된 JWT 데이터를 확인하세요"
            }
            CommonError.IOException, CommonError.ClientAbortException -> {
                // Broken Pipe 에러에 대해서는 WARN 처리로 변경
                if (ERROR_TITLE.contains("Broken pipe", true))
                    error.level = Level.WARN
            }
        }
        if (commonResponse == null)
            commonResponse = CommonResponse(error.errorCode.code, ERROR_TITLE, errorMap)

        // Level 별 에러메시지 출력
        ErrorUtils.logWriter(error, CLASS_NAME, ErrorUtils.getResErrorDTO(error.errorCode.code), ERROR_TITLE, errorMap, e, CUSTOM_MSG)
        // e.printStackTrace()

        return commonResponse
    }


    @ExceptionHandler(value = [
        Exception::class
    ])
    @Throws(Exception::class)
    fun allException(e: Exception, request: HttpServletRequest, webRequest: WebRequest): CommonResponse<Any> {
        val ERROR_TITLE: String = e.message ?: "Empty Error Message"
        val errorMap = ErrorUtils.setErrorMap(request, webRequest)
        // Level 별 에러메시지 출력
        ErrorUtils.errorWriter(CLASS_NAME, ErrorUtils.getResErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value()), ERROR_TITLE, errorMap, e)
        return CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value() ,e.message ?: HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase, errorMap)
    }

    /**
     * BindException Field 메세지 가공
     * @param bindingResult
     * @return
     */
    protected fun getBindResultFieldErrorMessage(bindingResult: BindingResult): String {
        val resultMap: LinkedHashMap<String, Any> = LinkedHashMap()
        resultMap["title"] = "Parameter Validation Error"
        val fieldErrorList = bindingResult.fieldErrors
        val paramList: MutableList<Map<String, Any?>> = ArrayList()
        for (fieldError in fieldErrorList) {
            val resultParam: LinkedHashMap<String, Any?> = linkedMapOf()
            resultParam[fieldError.field] = fieldError.rejectedValue
            resultParam["message"] = fieldError.defaultMessage
            paramList.add(resultParam)
            /*
            log.debug("## ERROR getField = {}", fieldError.getField());
            log.debug("## ERROR getRejectedValue = {}", fieldError.getRejectedValue());
            log.debug("## ERROR getArguments = {}", fieldError.getArguments());
            log.debug("## ERROR getCode = {}", fieldError.getCode());
            log.debug("## ERROR getCodes = {}", fieldError.getCodes());
            log.debug("## ERROR getObjectName = {}", fieldError.getObjectName());
            log.debug("## ERROR getDefaultMessage = {}", fieldError.getDefaultMessage());
            */
        }
        resultMap["fields"] = paramList
        return JsonUtils.toMapperPrettyJson(resultMap)
    }

}