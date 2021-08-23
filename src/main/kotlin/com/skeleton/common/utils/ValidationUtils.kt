package com.skeleton.common.utils

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.regex.Pattern
import javax.validation.ConstraintViolation
import javax.validation.Validation

/**
 * Created by KMS on 2021/05/26.
 */
class ValidationUtils {

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)

        /* Validator 객체 */
        private val validator = Validation.buildDefaultValidatorFactory().validator
        /**
         * 객체에서 요청 필드명과 일치한 Validation 검증 데이터를 가져온다
         * 검증데이터로 해당 검증을 통과했는지 여부를 판단할 수 있다
         * @param object
         * @param fieldName
         * @param <T>
         * @return
        </T> */
        fun <T> getValidData(`object`: T, fieldName: String): ConstraintViolation<T>? {
            val constraintValidations: Set<ConstraintViolation<T>?> =
                validator.validate(`object`)
            return constraintValidations.stream()
                .filter { data: ConstraintViolation<T>? ->
                    data!!.propertyPath.toString() == fieldName
                }
                .findFirst().orElse(null)
        }

        /**
         * 객체에 대한 Validation 을 검증해서
         * 검증을 통과하면 true 통과하지 못하면 false
         * @param object
         * @param <T>
        </T> */
        fun <T> isValid(`object`: T, fieldName: String): Boolean {
            val validData = getValidData(`object`, fieldName)
            return Objects.isNull(validData)
        }

        /**
         * 객체에 대한 Validation 을 검증해서
         * NotEmpty, NotNull 이 설정된 속성에 대한 검증을 통과하지 못하면 true 통과하면 false
         * @param object
         * @param <T>
        </T> */
        fun <T> isEmpty(`object`: T, fieldName: String): Boolean {
            val validData = getValidData(`object`, fieldName)
            if (Objects.isNull(validData)) return false
            val isValid =
                validData!!.messageTemplate.contains("NotEmpty") || validData.messageTemplate.contains("NotNull")
            if (isValid) log.error("필수 입력값 누락: {}", validData.propertyPath.toString() + " 은/는 필수 입력값 입니다")
            return isValid
        }

        /**
         * 객체에 대한 Validation 을 검증해서
         * NotEmpty, NotNull 이 설정된 속성에 대한 검증을 통과하면 true 통과하지 못하면 false
         * @param object
         * @param <T>
        </T> */
        fun <T> isNotEmpty(`object`: T, fieldName: String): Boolean {
            return !isEmpty(`object`, fieldName)
        }

        /**
         * IP 주소 형식 유효성 검증
         * @param data
         * @return
         */
        fun isIp(data: String?): Boolean {
            if (StringUtils.isEmpty(data)) return false
            val pattern =
                Pattern.compile("(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])")
            val matcher = pattern.matcher(data)
            return matcher.find()
        }

        /**
         * PORT 데이터 범위 65535 까지 아니면 false
         * @param data
         * @return
         */
        fun isPort(data: Int): Boolean {
            return if (Objects.isNull(data)) false else data <= 65535
        }
    }
}