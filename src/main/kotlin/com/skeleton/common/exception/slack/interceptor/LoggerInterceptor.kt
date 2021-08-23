package com.skeleton.common.exception.slack.interceptor

import com.skeleton.common.exception.slack.utils.MDCUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.ObjectUtils
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by KMS on 2021/05/20.
 */
@Component
class LoggerInterceptor: HandlerInterceptor {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        log.info("-----------------------------------------------------------------------------------------------------------------------------------------")
        val beforeMessage: String? = MDCUtils[MDCUtils.BEFORE_REQUEST_MESSAGE]
        if (ObjectUtils.isEmpty(beforeMessage)) {
            log.info(
                "Request ID=${MDCUtils[MDCUtils.REQUEST_ID]}, \tMethod=${MDCUtils[MDCUtils.REQUEST_METHOD_TYPE]}, \tRequest URI=${request.requestURI}"
            )
        } else {
            log.info(
                "Request ID=${MDCUtils[MDCUtils.REQUEST_ID]}, \tMethod=${MDCUtils[MDCUtils.REQUEST_METHOD_TYPE]}, \t${beforeMessage}"
            )
        }

        if (!ObjectUtils.isEmpty(MDCUtils[MDCUtils.PARAMETER_MAP_MDC])) {
            log.info("\n${MDCUtils[MDCUtils.PARAMETER_MAP_MDC]}")
        }

        return super.preHandle(request, response, handler)
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        log.info("=========================================================================================================================================")
        super.postHandle(request, response, handler, modelAndView)
    }
}