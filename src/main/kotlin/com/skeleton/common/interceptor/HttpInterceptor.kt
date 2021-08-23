package com.skeleton.common.interceptor

import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 인터셉터
 * Created by KMS on 2021/04/15.
 */
@Component
class HttpInterceptor : HandlerInterceptor {

    val log = LogManager.getLogger()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        return true
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        // log.info("3. from PostHandle method.")
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        // log.info("4. from AfterCompletion method - Request Completed!")
    }
}