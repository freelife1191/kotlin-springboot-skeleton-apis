package com.skeleton.common.filter

import com.google.common.base.CaseFormat
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.Collections

import java.util.Enumeration

import javax.servlet.http.HttpServletRequestWrapper

import java.util.concurrent.ConcurrentHashMap

import javax.servlet.FilterChain

import javax.servlet.http.HttpServletResponse

import javax.servlet.http.HttpServletRequest

import org.springframework.web.filter.OncePerRequestFilter
import java.lang.Exception

/**
 * SnakeCase Parameter to CamelCase Parameter Filter
 * Created by KMS on 2021/04/15.
 */
@Configuration
class SnakeCaseToCamelParameterFilter {

    val log: Logger = LogManager.getLogger()

    @Bean
    @Throws(Exception::class) //ServletException::class, IOException::class
    fun snakeCaseConverterFilter(): OncePerRequestFilter? {
        return object : OncePerRequestFilter() {
            override fun doFilterInternal(
                request: HttpServletRequest,
                response: HttpServletResponse,
                filterChain: FilterChain
            ) {
                val parameters: MutableMap<String, Array<String>> = ConcurrentHashMap()
                for (param in request.parameterMap.keys) {
                    val camelCaseParam: String = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, param)
                    parameters[camelCaseParam] = request.getParameterValues(param)
                    parameters[param] = request.getParameterValues(param)
                }
                filterChain.doFilter(
                    object : HttpServletRequestWrapper(request) {
                        override fun getParameter(name: String): String? {
                            return if (parameters.containsKey(name)) parameters[name]?.get(0) else null
                        }

                        override fun getParameterNames(): Enumeration<String> {
                            return Collections.enumeration(parameters.keys)
                        }

                        override fun getParameterValues(name: String): Array<String>? {
                            /*
                            if(parameters[name] != null) {
                                for (parameter in parameters[name]!!) {
                                    println("#### parameters[${name}] = ${parameter}")
                                }
                            }
                            */

//                            return parameters[name] ?: arrayOf()
                            return parameters[name]
                        }

                        override fun getParameterMap(): Map<String, Array<String>> {
                            return parameters
                        }
                    }, response)
            }
        }
    }

}