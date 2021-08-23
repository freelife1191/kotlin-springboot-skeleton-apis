package com.skeleton.common.exception.slack.utils

import com.skeleton.common.utils.JsonUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.slf4j.MDC

/**
 * httpRequest가 존재하는 동안 데이터를 유지시키기 위한 유틸
 * http://logback.qos.ch/manual/mdc.html
 *
 * Created by KMS on 2021/05/20.
 */
class MDCUtils {

    companion object {
        private val log: Logger = LogManager.getLogger()

        private val mdc = MDC.getMDCAdapter()

        /**
         * The constant REQUEST_ID.
         */
        val REQUEST_ID = "REQUEST_ID"

        /**
         * The constant REQUEST_SESSION_ID.
         */
        val REQUEST_SESSION_ID = "REQUEST_SESSION_ID"

        /**
         * The constant HEADER_MAP_MDC.
         */
        val HEADER_MAP_MDC = "HEADER_MAP_MDC"

        /**
         * The constant PARAMETER_MAP_MDC.
         */
        val PARAMETER_MAP_MDC = "PARAMETER_MAP_MDC"

        /**
         * The constant USER_INFO_MDC.
         */
        val USER_INFO_MDC = "USER_INFO_MDC"

        /**
         * The constant REQUEST_URI_MDC.
         */
        val REQUEST_URI_MDC = "REQUEST_URI_MDC"

        /**
         * The constant REQUEST_METHOD_TYPE.
         */
        val REQUEST_METHOD_TYPE = "REQUEST_METHOD_TYPE"

        /**
         * The constant AGENT_DETAIL_MDC.
         */
        val AGENT_DETAIL_MDC = "AGENT_DETAIL_MDC"

        /**
         * The constant BEFORE_REQUEST_MESSAGE.
         */
        val BEFORE_REQUEST_MESSAGE = "BEFORE_REQUEST_MESSAGE"

        /**
         * Set.
         *
         * @param key   the key
         * @param value the value
         */
        operator fun set(key: String, value: String) {
            mdc.put(key, value)
        }

        /**
         * Sets json value.
         *
         * @param key   the key
         * @param value the value
         */
        fun setJsonValue(key: String, value: Any) {
            mdc.put(key, JsonUtils.toJson(value))
        }

        /**
         * Get string.
         *
         * @param key the key
         * @return the string
         */
        operator fun get(key: String): String? {
            return mdc[key]
        }

        /**
         * Clear.
         */
        fun clear() {
            MDC.clear()
        }

        /**
         * Sets error attribute.
         *
         * @param errorAttribute the error attribute
         */
        fun setErrorAttribute(errorAttribute: Map<String, Any>) {
            if (errorAttribute.containsKey("path")) {
                set(REQUEST_URI_MDC, errorAttribute["path"] as String)
            }
        }
    }

}