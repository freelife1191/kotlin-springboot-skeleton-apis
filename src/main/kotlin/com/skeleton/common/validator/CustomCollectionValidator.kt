package com.skeleton.common.validator

import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.SpringValidatorAdapter
import javax.validation.Validation

/**
 * Collection 타입에 대한 Validation 체크
 * 참조1: https://github.com/HomoEfficio/dev-tips/wiki/SpringMVC%EC%97%90%EC%84%9C-Collection%EC%9D%98-Validation
 * 참조2: https://gompangs.tistory.com/entry/Spring-Valid-Collection-Validation-%EA%B4%80%EB%A0%A8
 *
 * 사용예시
 * private final CustomCollectionValidator customCollectionValidator;
 *
 * // Collection 의 경우 @Valid 로 유효성 검증이 되지 않아 직접 validate
 * customCollectionValidator.validate(idList, bindingResult);
 *
 * // Collection 유효성 검증 실패에 대한 예외처리
 * if (bindingResult.hasErrors())
 *    throw new BindException(bindingResult);
 *
 * Created by KMS on 2021/05/22.
 */
@Component
class CustomCollectionValidator(
    private var validator: SpringValidatorAdapter
): Validator {

    init {
        this.validator = SpringValidatorAdapter(
            Validation.buildDefaultValidatorFactory().validator
        )
    }

    override fun supports(clazz: Class<*>): Boolean = true

    override fun validate(target: Any, errors: Errors) {
        when (target) {
            is Collection<*> -> {
                for (obj in target) {
                    if(obj != null) validator.validate(obj, errors)
                }
            }
            else -> validator.validate(target, errors)
        }
    }
}