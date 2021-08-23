package com.skeleton.common.exception.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.skeleton.common.exception.domain.dto.ResErrorDTO
import com.skeleton.common.exception.domain.enums.CommonError
import com.skeleton.common.exception.domain.enums.ErrorCode
import com.skeleton.common.utils.JsonUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import org.springframework.http.HttpStatus
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.WebRequest
import java.util.HashMap
import javax.servlet.http.HttpServletRequest

/**
 * Created by KMS on 2021/05/14.
 */
class ErrorUtils {

    companion object {

        val log: Logger = LoggerFactory.getLogger(this::class.java)

        /**
         * ERROR CODE 로 ResErrorDTO 응답 객체 생성
         * @param code Int
         * @return ResErrorDTO
         */
        fun getResErrorDTO(code: Int): ResErrorDTO {
            val errorCode = ErrorCode.getErrorCode(code)
            if(errorCode != null)
                return ResErrorDTO(errorCode.code, errorCode.message)
            val httpStatus = HttpStatus.valueOf(code)
            return ResErrorDTO(httpStatus.value(), httpStatus.reasonPhrase)
        }

        /**
         * Exception Handler ErrorMap 기본 셋팅
         * @param request
         * @throws IOException
         */
        @Throws(Exception::class)
        fun setErrorMap(request: HttpServletRequest, webRequest: WebRequest): Map<String, Any> {
            return setErrorMap(request, webRequest, linkedMapOf(), "")
        }

        /**
         * Exception Handler ErrorMap 기본 셋팅
         * @param request
         * @throws IOException
         */
        @Throws(Exception::class)
        fun setErrorMap(request: HttpServletRequest, webRequest: WebRequest, secretKey: String): Map<String, Any> {
            return setErrorMap(request, webRequest, linkedMapOf(), secretKey)
        }

        /**
         * Exception Handler ErrorMap 기본 셋팅
         * @param request
         * @throws IOException
         */
        @Throws(Exception::class)
        fun setErrorMap(
            request: HttpServletRequest,
            webRequest: WebRequest,
            reqErrorMap: LinkedHashMap<String, Any>,
            secretKey: String
        ): Map<String, Any> {
            val objectMapper = ObjectMapper()
            val errorMap = getErrorMap(reqErrorMap, request) //에러 맵 생성
            /*
            if (StringUtils.isNotEmpty(secretKey)) {
                try {
                    val userId: String = AuthUtils.getUserId(request.getHeader("jwt"), secretKey)
                    errorMap["Request USER ID"] = userId
                } catch (e: Exception) {
                    log.error("JWT 복호화 해제 실패: ERROR_MSG = {}, JWT = {}", e.message, request.getHeader("jwt"))
                }
            }
            */

            var body = webRequest.getAttribute("body", RequestAttributes.SCOPE_REQUEST)
            if (body is String) body = objectMapper.readValue(body, MutableMap::class.java)
            if(body != null)
                errorMap["Request Body"] = body
            return errorMap
        }

        /**
         * Header map map.
         *
         * @return the map
         */
        private fun headerMap(request: HttpServletRequest): Map<String, String> {
            val convertedHeaderMap: MutableMap<String, String> = HashMap()
            val headerMap = request.headerNames
            while (headerMap.hasMoreElements()) {
                val name = headerMap.nextElement()
                val value: String = request.getHeader(name)
                convertedHeaderMap[name] = value
            }
            return convertedHeaderMap
        }

        /**
         * Error Map 생성
         * 에러 메세지 맵을 생성하여 메신저에 전송한다
         * @param errorMap
         * @param request
         * @return
         */
        fun getErrorMap(reqErrorMap: LinkedHashMap<String, Any>, request: HttpServletRequest): LinkedHashMap<String, Any>{
            val errorMap: LinkedHashMap<String, Any> = reqErrorMap
            // if (request.getHeader("jwt") != null) errorMap["Request JWT"] = request.getHeader("jwt")
            // if (request.getHeader("Authorization") != null) errorMap["Request Authorization"] = request.getHeader("Authorization")
            // errorMap["Headers"] = headerMap(request)
            if (request.getHeader("Content-Type") != null) errorMap["Content-Type"] = request.getHeader("Content-Type")
            // if (request.getHeader("Accept") != null) errorMap["Accept"] = request.getHeader("Accept")
            // if (request.getHeader("Referer") != null) errorMap["Referer"] = request.getHeader("Referer")
            // if (request.getHeader("User-Agent") != null) errorMap["User-Agent"] = request.getHeader("User-Agent")

            errorMap["Request URI"] = request.requestURI
            errorMap["HttpMethod"] = request.method
            errorMap["Servlet Path"] = request.servletPath
            errorMap["Client IP"] = request.localAddr
            errorMap["QueryString"] = request.queryString ?: ""
            errorMap["Parameters"] = request.parameterMap

            return errorMap
        }

        /**
         * Level 별 에러메세지 출력
         * @param error
         * @param CLASS_NAME
         * @param ERROR_TITLE
         * @param errorMap
         * @param e
         */
        fun logWriter(
            error: CommonError,
            CLASS_NAME: String,
            resErrorDTO: ResErrorDTO,
            ERROR_TITLE: String,
            errorMap: Map<String, Any>,
            e: Exception
        ) {
            logWriter(error, CLASS_NAME, resErrorDTO, ERROR_TITLE, errorMap, e, "")
        }

        /**
         * Level 별 에러메세지 출력
         * @param error
         * @param CLASS_NAME
         * @param ERROR_TITLE
         * @param errorMap
         * @param e
         */
        fun logWriter(
            error: CommonError,
            CLASS_NAME: String,
            resErrorDTO: ResErrorDTO,
            ERROR_TITLE: String,
            errorMap: Map<String, Any>,
            e: Exception,
            CUSTOM_MSG: String?
        ) {
            val exceptionMessage = if(CUSTOM_MSG.isNullOrEmpty()) ExceptionUtils.getMessage(e) else "$CUSTOM_MSG\n${ExceptionUtils.getMessage(e)}"

            when (error.level) {
                Level.INFO -> infoWriter(CLASS_NAME, resErrorDTO, ERROR_TITLE, errorMap, exceptionMessage)
                Level.WARN -> warnWriter(CLASS_NAME, resErrorDTO, ERROR_TITLE, errorMap, exceptionMessage)
                Level.ERROR -> {
                    if(error.exception)
                        errorWriter(CLASS_NAME, resErrorDTO, ERROR_TITLE, errorMap, exceptionMessage, e)
                    else errorWriter(CLASS_NAME, resErrorDTO, ERROR_TITLE, errorMap, exceptionMessage)
                }
            }
        }

        /**
         * 에러메세지 출력
         * @param CLASS_NAME
         * @param ERROR_TITLE
         * @param errorMap
         * @param e
         */
        fun errorWriter(CLASS_NAME: String, resErrorDTO: ResErrorDTO, ERROR_TITLE: String, errorMap: Map<String, Any>, e: Exception) {
            log.error("[$CLASS_NAME] ERROR 메세지 :: $ERROR_TITLE\n\n응답 코드 :: ${resErrorDTO.code}\n응답 메세지 :: ${resErrorDTO.message}\n요청 정보 ::\n" +
                    "${JsonUtils.toMapperPrettyJson(errorMap)}\n\nException ::\n${e.message}", e)
        }

        /**
         * 에러메세지 출력
         * @param CLASS_NAME
         * @param ERROR_TITLE
         * @param errorMap
         * @param errorMsg
         */
        fun errorWriter(CLASS_NAME: String, resErrorDTO: ResErrorDTO, ERROR_TITLE: String, errorMap: Map<String, Any>, errorMsg: String) {
            log.error("[$CLASS_NAME] ERROR 메세지 :: $ERROR_TITLE\n\n응답 코드 :: ${resErrorDTO.code}\n응답 메세지 :: ${resErrorDTO.message}\n요청 정보 ::\n" +
                    "${JsonUtils.toMapperPrettyJson(errorMap)}\n\nException ::\n${errorMsg}")
        }

        /**
         * 에러메세지 출력
         * @param CLASS_NAME
         * @param ERROR_TITLE
         * @param errorMap
         * @param errorMsg
         */
        fun errorWriter(CLASS_NAME: String, resErrorDTO: ResErrorDTO, ERROR_TITLE: String, errorMap: Map<String, Any>, errorMsg: String, e: Exception) {
            log.error("[$CLASS_NAME] ERROR 메세지 :: $ERROR_TITLE\n\n응답 코드 :: ${resErrorDTO.code}\n응답 메세지 :: ${resErrorDTO.message}\n요청 정보 ::\n" +
                    "${JsonUtils.toMapperPrettyJson(errorMap)}\n\nException ::\n${errorMsg}", e)
        }

        /**
         * WARN 에러메세지 출력
         * @param CLASS_NAME
         * @param ERROR_TITLE
         * @param errorMap
         * @param errorMsg
         */
        fun warnWriter(CLASS_NAME: String, resErrorDTO: ResErrorDTO, ERROR_TITLE: String, errorMap: Map<String, Any>, errorMsg: String) {
            log.warn("[$CLASS_NAME] WARN 메세지 :: $ERROR_TITLE\n\n응답 코드 :: ${resErrorDTO.code}\n응답 메세지 :: ${resErrorDTO.message}\n요청 정보 ::\n" +
                    "${JsonUtils.toMapperPrettyJson(errorMap)}\n\nException ::\n${errorMsg}")
        }

        /**
         * INFO 에러메세지 출력
         * @param CLASS_NAME
         * @param ERROR_TITLE
         * @param errorMap
         * @param errorMsg
         */
        fun infoWriter(CLASS_NAME: String, resErrorDTO: ResErrorDTO, ERROR_TITLE: String, errorMap: Map<String, Any>, errorMsg: String) {
            log.info("[$CLASS_NAME] INFO 메세지 :: $ERROR_TITLE\n\n응답 코드 :: ${resErrorDTO.code}\n응답 메세지 :: ${resErrorDTO.message}\n요청 정보 ::\n" +
                    "${JsonUtils.toMapperPrettyJson(errorMap)}\n\nException ::\n${errorMsg}")
        }
    }
}