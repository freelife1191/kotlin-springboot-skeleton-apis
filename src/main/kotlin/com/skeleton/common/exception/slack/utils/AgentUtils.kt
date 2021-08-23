package com.skeleton.common.exception.slack.utils

import eu.bitwalker.useragentutils.*
import javax.servlet.http.HttpServletRequest

/**
 * The type Agent utils.
 *
 * Created by KMS on 2021/05/20.
 */
class AgentUtils {

    companion object {
        /**
         * Gets user agent string.
         *
         * @param request the request
         * @return the user agent string
         */
        fun getUserAgentString(request: HttpServletRequest): String {
            return request.getHeader("User-Agent")
        }

        /**
         * Gets user agent.
         *
         * @param request the request
         * @return the user agent
         */
        fun getUserAgent(request: HttpServletRequest): UserAgent {
            val userAgentString = getUserAgentString(request)
            return UserAgent.parseUserAgentString(userAgentString)
        }

        /**
         * Gets user os.
         *
         * @param request the request
         * @return the user os
         */
        fun getUserOs(request: HttpServletRequest): OperatingSystem {
            val userAgent: UserAgent = getUserAgent(request)
            return userAgent.operatingSystem
        }

        /**
         * Gets browser.
         *
         * @param request the request
         * @return the browser
         */
        fun getBrowser(request: HttpServletRequest): Browser {
            val userAgent: UserAgent = getUserAgent(request)
            return userAgent.browser
        }

        /**
         * Gets browser version.
         *
         * @param request the request
         * @return the browser version
         */
        fun getBrowserVersion(request: HttpServletRequest): Version? {
            val userAgent: UserAgent = getUserAgent(request)
            return userAgent.browserVersion
        }

        /**
         * Gets browser type.
         *
         * @param request the request
         * @return the browser type
         */
        fun getBrowserType(request: HttpServletRequest): BrowserType {
            val browser: Browser = getBrowser(request)
            return browser.browserType
        }

        /**
         * Gets rendering engine.
         *
         * @param request the request
         * @return the rendering engine
         */
        fun getRenderingEngine(request: HttpServletRequest): RenderingEngine {
            val browser: Browser = getBrowser(request)
            return browser.renderingEngine
        }

        /**
         * Gets device type.
         *
         * @param request the request
         * @return the device type
         */
        fun getDeviceType(request: HttpServletRequest): DeviceType {
            val operatingSystem: OperatingSystem = getUserOs(request)
            return operatingSystem.deviceType
        }

        /**
         * Gets manufacturer.
         *
         * @param request the request
         * @return the manufacturer
         */
        fun getManufacturer(request: HttpServletRequest): Manufacturer {
            val operatingSystem: OperatingSystem = getUserOs(request)
            return operatingSystem.manufacturer
        }

        /**
         * Gets agent detail.
         *
         * @param request the request
         * @return the agent detail
         */
        fun getAgentDetail(request: HttpServletRequest): Map<String, String> {
            val agentDetail: MutableMap<String, String> = HashMap()
            agentDetail["browser"] = getBrowser(request).toString()
            agentDetail["browserType"] = getBrowserType(request).toString()
            //agentDetail.put("browserVersion", getBrowserVersion(request).toString());
            agentDetail["renderingEngine"] = getRenderingEngine(request).toString()
            agentDetail["os"] = getUserOs(request).toString()
            agentDetail["deviceType"] = getDeviceType(request).toString()
            agentDetail["manufacturer"] = getManufacturer(request).toString()
            return agentDetail
        }
    }
}