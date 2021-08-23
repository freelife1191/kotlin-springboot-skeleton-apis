package com.skeleton.common.exception.slack.filter

import com.skeleton.common.exception.slack.utils.AgentUtils
import com.skeleton.common.exception.slack.utils.MDCUtils
import com.skeleton.common.exception.slack.utils.MDCUtils.Companion.AGENT_DETAIL_MDC
import com.skeleton.common.exception.slack.utils.MDCUtils.Companion.BEFORE_REQUEST_MESSAGE
import com.skeleton.common.exception.slack.utils.MDCUtils.Companion.HEADER_MAP_MDC
import com.skeleton.common.exception.slack.utils.MDCUtils.Companion.PARAMETER_MAP_MDC
import com.skeleton.common.exception.slack.utils.MDCUtils.Companion.REQUEST_ID
import com.skeleton.common.exception.slack.utils.MDCUtils.Companion.REQUEST_METHOD_TYPE
import com.skeleton.common.exception.slack.utils.MDCUtils.Companion.REQUEST_SESSION_ID
import com.skeleton.common.exception.slack.utils.MDCUtils.Companion.REQUEST_URI_MDC
import com.skeleton.common.utils.JsonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.util.ObjectUtils
import org.springframework.web.filter.AbstractRequestLoggingFilter
import java.io.IOException
import java.util.HashMap
import java.util.concurrent.atomic.AtomicLong
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by KMS on 2021/05/20.
 */
open class HttpLoggingFilter: AbstractRequestLoggingFilter() {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    private val id = AtomicLong(1)

    private var reqId: Long = 0

    override fun beforeRequest(request: HttpServletRequest, message: String) {
        if (!ObjectUtils.isEmpty(request.queryString)) {
            MDCUtils[BEFORE_REQUEST_MESSAGE] = message
        }
    }

    override fun afterRequest(request: HttpServletRequest, message: String) {
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (includeFilter(request) && excludeFilter(request)) {
            reqId = id.incrementAndGet()
            // val requestLoggingWrapper = RequestLoggingWrapper(reqId, request)
            // val startTime: Long = requestLoggingWrapper.getStartTime()
            MDCUtils[REQUEST_METHOD_TYPE] = request.method
            MDCUtils[PARAMETER_MAP_MDC] = JsonUtils.toMapperJson(request.parameterMap)
            MDCUtils.setJsonValue(REQUEST_ID, reqId)
            if(request.requestedSessionId != null) MDCUtils.setJsonValue(REQUEST_SESSION_ID, request.requestedSessionId)
            // Set Http Header
            MDCUtils.setJsonValue(HEADER_MAP_MDC, headerMap(request))

            // Set Agent Detail
            MDCUtils.setJsonValue(AGENT_DETAIL_MDC, AgentUtils.getAgentDetail(request))

            // Set Http Request URI
            MDCUtils[REQUEST_URI_MDC] = request.requestURI
            /*
            val responseLoggingWrapper = ResponseLoggingWrapper(reqId, startTime, response, request.requestURI)
            try {
                super.doFilterInternal(requestLoggingWrapper, responseLoggingWrapper, filterChain)
            } finally {
                MDC.clear()
            }
            */
            MDC.clear()
            filterChain.doFilter(request, response)
        } else {
            filterChain.doFilter(request, response)
        }
    }

    /**
     * 허용 필터 항목
     * @param request HttpServletRequest
     * @return Boolean
     */
    private fun includeFilter(request: HttpServletRequest): Boolean =
        listOf(
            request.requestURI.contains("api")
        ).all { it }

    /**
     * 제외 필터 항목
     * @param request HttpServletRequest
     * @return Boolean
     */
    private fun excludeFilter(request: HttpServletRequest): Boolean {
        return !listOf(
            // request.contentType.startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE),
            request.requestURI.contains("excel")
        ).all { it }
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
}