package com.skeleton.common.exception.slack.filter

import com.skeleton.common.exception.slack.utils.MDCUtils
import com.skeleton.common.exception.slack.utils.MDCUtils.Companion.PARAMETER_MAP_MDC
import com.skeleton.common.exception.slack.utils.MDCUtils.Companion.REQUEST_METHOD_TYPE
import org.apache.commons.io.IOUtils
import org.springframework.http.HttpMethod
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

/**
 * Created by KMS on 2021/05/20.
 */
class RequestLoggingWrapper(
    private val request: HttpServletRequest,
) : HttpServletRequestWrapper(request) {

    private val bos = ByteArrayOutputStream()
    private var id: Long = 0
    private lateinit var servletInputStream: ServletInputStream
    private var parameterMap = mutableMapOf<String, Array<String>>()

    /**
     * Instantiates a new Request logging wrapper.
     *
     * @param requestId the request id
     * @param request   the request
     */
    @Throws(IOException::class)
    constructor(requestId: Long, request: HttpServletRequest) : this(request) {
        id = requestId
        parameterMap.putAll(super@RequestLoggingWrapper.getParameterMap())
        servletInputStream = super@RequestLoggingWrapper.getInputStream()
        IOUtils.copy(servletInputStream, bos)
        logRequest()
    }

    override fun getParameterMap(): MutableMap<String, Array<String>> {
        return parameterMap
    }

    override fun getInputStream(): ServletInputStream {
        return object : ServletInputStream() {
            var input = ByteArrayInputStream(bos.toByteArray())
            override fun read(): Int {
                return input.read()
            }

            override fun read(b: ByteArray, off: Int, len: Int): Int {
                return input.read(b, off, len)
            }

            @Throws(IOException::class)
            override fun read(b: ByteArray): Int {
                return input.read(b)
            }

            override fun isFinished(): Boolean {
                return servletInputStream.isFinished
            }

            override fun isReady(): Boolean {
                return servletInputStream.isReady
            }

            override fun setReadListener(readListener: ReadListener) {
                servletInputStream.setReadListener(readListener)
            }
        }
    }

    private fun logRequest() {
        MDCUtils[REQUEST_METHOD_TYPE] = method
        if (method != HttpMethod.GET.name && toByteArray() != null && toByteArray()!!.isNotEmpty()) {
            val params = String(toByteArray()!!)
            MDCUtils[PARAMETER_MAP_MDC] = params
        }
    }

    private fun toByteArray(): ByteArray? {
        return bos.toByteArray()
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    fun getId(): Long {
        return id
    }

    /**
     * Gets start time.
     *
     * @return the start time
     */
    fun getStartTime(): Long {
        return System.currentTimeMillis()
    }

    /**
     * Header map map.
     *
     * @return the map
     */
    fun headerMap(): Map<String, String> {
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