package com.skeleton.common.exception.slack.appender

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.StackTraceElementProxy
import ch.qos.logback.core.UnsynchronizedAppenderBase
import ch.qos.logback.core.util.ContextUtil
import com.skeleton.common.exception.slack.utils.MDCUtils
import com.skeleton.common.utils.JsonUtils
import net.gpedro.integrations.slack.SlackApi
import net.gpedro.integrations.slack.SlackAttachment
import net.gpedro.integrations.slack.SlackField
import net.gpedro.integrations.slack.SlackMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.ObjectUtils
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.net.SocketException
import java.net.UnknownHostException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * Created by KMS on 2021/05/20.
 */
class SlackAppender(
    private var enabled: Boolean = false,
    private var token: String? = null,
    private var channel: String? = null,
    private var level: Level? = null,
    private var title: String? = null,
    private var botName: String? = null,
    // https://slackmojis.com/
    private var botEmoji: String? = null
): UnsynchronizedAppenderBase<ILoggingEvent>() {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    // @Value("\${spring.application.name}")
    // val appName: String? =null

    override fun doAppend(eventObject: ILoggingEvent?) {
        super.doAppend(eventObject)
    }

    override fun append(loggingEvent: ILoggingEvent) {
        if (loggingEvent.level.isGreaterOrEqual(level)) {
            if (enabled) {
                toSlack(loggingEvent)
            }
        }
    }

    private fun toSlack(loggingEvent: ILoggingEvent) {
        if (loggingEvent.level.isGreaterOrEqual(level)) {
            val fields: MutableList<SlackField> = ArrayList()
            val slackAttachment = SlackAttachment()
            slackAttachment.setFallback("장애 발생")
            slackAttachment.setColor("danger")
            slackAttachment.setTitle(loggingEvent.formattedMessage)

            /*
            val path = SlackField()
            path.setTitle("요청 URL")
            path.setValue(MDCUtils[MDCUtils.REQUEST_URI_MDC])
            path.isShorten = false
            fields.add(path)
            */

            /*
            val issueRating = SlackField()
            issueRating.setTitle("장애 등급")
            issueRating.isShorten = true
            issueRating.setValue("등급 분류 필요")
            if (loggingEvent.throwableProxy != null) {
                slackAttachment.setText(getStackTrace(loggingEvent.throwableProxy.stackTraceElementProxyArray))
            }
            fields.add(issueRating)
            */

            /*
            val userIP = SlackField()
            userIP.setTitle("사용자 IP")
            userIP.setValue(getHostIp())
            userIP.isShorten = true
            fields.add(userIP)
            */

            if (!ObjectUtils.isEmpty(MDCUtils[MDCUtils.AGENT_DETAIL_MDC])) {
                val agentDetail = SlackField()
                agentDetail.setTitle("사용자 환경정보")
                agentDetail.setValue(JsonUtils.toPrettyJson(MDCUtils[MDCUtils.AGENT_DETAIL_MDC]))
                agentDetail.isShorten = false
                fields.add(agentDetail)
            }

            val date = SlackField()
            date.setTitle("발생 시간")
            date.setValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            date.isShorten = true
            fields.add(date)

            val hostName = SlackField()
            hostName.setTitle("호스트명")
            hostName.setValue(getHostName())
            hostName.isShorten = true
            fields.add(hostName)

            if (!ObjectUtils.isEmpty(MDCUtils[MDCUtils.HEADER_MAP_MDC])) {
                val headerInformation = SlackField()
                headerInformation.setTitle("Http Header 정보")
                headerInformation.setValue(JsonUtils.toPrettyJson(MDCUtils[MDCUtils.HEADER_MAP_MDC]))
                headerInformation.isShorten = false
                fields.add(headerInformation)
            }
            if (!ObjectUtils.isEmpty(MDCUtils[MDCUtils.PARAMETER_MAP_MDC])) {
                val bodyInformation = SlackField()
                bodyInformation.setTitle("Http Body 정보")
                bodyInformation.setValue(MDCUtils[MDCUtils.PARAMETER_MAP_MDC])
                bodyInformation.isShorten = false
                fields.add(bodyInformation)
            }
            slackAttachment.setFields(fields)

            val slackMessage = SlackMessage("")
            slackMessage.setChannel("#$channel")
            slackMessage.setUsername("${getBotName()} - ${getTitle()}")
            // slackMessage.setIcon(botEmoji)
            slackMessage.setIcon(getBotEmoji())
            slackMessage.setAttachments(listOf(slackAttachment))
            val slackApi = SlackApi(token)
            slackApi.call(slackMessage)
        }
    }

    /**
     * Gets host name.
     *
     * @return the host name
     */
    fun getHostName(): String? {
        try {
            return ContextUtil.getLocalHostName()
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * Gets ip.
     *
     * @return the host ip
     */
    fun getHostIp(): String? {
        try {
            val attributes = RequestContextHolder.getRequestAttributes()
            if (RequestContextHolder.getRequestAttributes() != null) {
                val request = (attributes as ServletRequestAttributes).request
                return request.remoteAddr
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * Gets Brower.
     *
     * @return the host Brower
     */
    fun getBrower(): String {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        // 에이전트
        val agent = request.getHeader("User-Agent")
        // 브라우져 구분
        var brower = ""
        if (agent != null) {
            if (agent.indexOf("Trident") > -1) {
                brower = "MSIE"
            } else if (agent.indexOf("Chrome") > -1) {
                brower = "Chrome"
            } else if (agent.indexOf("Opera") > -1) {
                brower = "Opera"
            } else if (agent.indexOf("iPhone") > -1 && agent.indexOf("Mobile") > -1) {
                brower = "iPhone"
            } else if (agent.indexOf("Android") > -1 && agent.indexOf("Mobile") > -1) {
                brower = "Android"
            }
        }
        return brower
    }

    /**
     * Gets stack trace.
     *
     * @param stackTraceElements the stack trace elements
     * @return the stack trace
     */
    fun getStackTrace(stackTraceElements: Array<StackTraceElementProxy>?): String? {
        if (stackTraceElements == null || stackTraceElements.isEmpty()) {
            return null
        }
        val sb = StringBuilder()
        for (element in stackTraceElements) {
            sb.append(element.toString())
            sb.append("\n")
        }
        return sb.toString()
    }


    /**
     * Is enabled boolean.
     *
     * @return the boolean
     */
    fun isEnabled(): Boolean {
        return enabled
    }

    /**
     * Sets enabled.
     *
     * @param enabled the enabled
     */
    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    /**
     * Gets level.
     *
     * @return the level
     */
    fun getLevel(): Level? {
        return level
    }

    /**
     * Sets level.
     *
     * @param level the level
     */
    fun setLevel(level: Level?) {
        this.level = level
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    fun getToken(): String? {
        return token
    }

    /**
     * Sets token.
     *
     * @param token the token
     */
    fun setToken(token: String?) {
        this.token = token
    }

    /**
     * Gets channel.
     *
     * @return the channel
     */
    fun getChannel(): String? {
        return channel
    }

    /**
     * Sets channel.
     *
     * @param channel the channel
     */
    fun setChannel(channel: String?) {
        this.channel = channel
    }

    fun getTitle(): String? {
        return if(ObjectUtils.isEmpty(title)) "서비스 장애 감지" else title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getBotName(): String? {
        return if(ObjectUtils.isEmpty(botName)) "App" else botName
    }

    fun setBotName(botName: String?) {
        this.botName = botName
    }

    fun getBotEmoji(): String? {
        return if(ObjectUtils.isEmpty(botEmoji)) ":exclamation:" else botEmoji
    }

    fun setBotEmoji(botEmoji: String?) {
        this.botEmoji = botEmoji
    }

}