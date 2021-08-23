package com.skeleton.common.exception.slack.filter

import com.skeleton.common.utils.JsonUtils
import org.apache.commons.io.output.TeeOutputStream
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

/**
 * Created by KMS on 2021/05/20.
 */
class ResponseLoggingWrapper(
    private val response: HttpServletResponse
): HttpServletResponseWrapper(response) {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    private val bos = ByteArrayOutputStream()
    private var id: Long = 0
    private var startTime: Long = 0
    private var requestUri: String? = null

    /**
     * Instantiates a new Response logging wrapper.
     *
     * @param id        the id
     * @param startTime the start time
     * @param response  the response
     */
    constructor(id: Long, startTime: Long, response: HttpServletResponse, requestUri: String) : this(response) {
        this.id = id
        this.startTime = startTime
        this.requestUri = requestUri
    }

    @Throws(IOException::class)
    override fun getOutputStream(): ServletOutputStream {
        val servletOutputStream = super@ResponseLoggingWrapper.getOutputStream()
        return object : ServletOutputStream() {
            private val tee = TeeOutputStream(servletOutputStream, bos)

            @Throws(IOException::class)
            override fun write(b: ByteArray) {
                tee.write(b)
            }

            @Throws(IOException::class)
            override fun write(b: ByteArray, off: Int, len: Int) {
                tee.write(b, off, len)
            }

            @Throws(IOException::class)
            override fun flush() {
                tee.flush()
                //  logRequest();
            }

            @Throws(IOException::class)
            override fun write(b: Int) {
                tee.write(b)
            }

            override fun isReady(): Boolean {
                return servletOutputStream.isReady
            }

            override fun setWriteListener(writeListener: WriteListener) {
                servletOutputStream.setWriteListener(writeListener)
            }

            @Throws(IOException::class)
            override fun close() {
                super.close()
                // do the logging
                logRequest()
            }
        }
    }

    private fun logRequest() {
        val toLog = toByteArray()
        if (toLog != null && toLog.isNotEmpty()) {
            val time = (System.currentTimeMillis() - getStartTime()).toDouble()
            val remake = String(toLog)
            log.debug(
                "Request ID={}, Request URI={}, totalTime={}, \n{}",
                getId(),
                requestUri,
                (time / 1000).toString() + "초",
                JsonUtils.toPrettyJson(remake)
            )
        }
    }

    /**
     * this method will clear the buffer, so
     *
     * @return captured bytes from stream
     */
    private fun toByteArray(): ByteArray? {
        val ret = bos.toByteArray()
        bos.reset()
        return ret
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    private fun getId(): Long {
        return id
    }

    /**
     * Gets start time.
     *
     * @return the start time
     */
    private fun getStartTime(): Long {
        return startTime
    }
}