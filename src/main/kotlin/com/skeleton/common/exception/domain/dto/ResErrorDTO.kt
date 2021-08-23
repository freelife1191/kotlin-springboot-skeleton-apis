package com.skeleton.common.exception.domain.dto

/**
 * Error 응답 클래스 설계
 * https://jsonobject.tistory.com/501
 *
 * 실제 REST API를 소비할 클라이언트에게 예외를 출력할 응답 클래스를 작성할 차례이다
 * 예외 발생시 응답 필드는 errorCode, errorMessage 2개로 한정한다
 * errorCode는 아래 설명할 사전 정의된 오류 코드를 의미하며, errorMessage는 사람이 인지할 수 있는 해당 오류 코드에 대한 설명을 의미한다
 *
 * 예외 출력의 관건은 절대로 애플리케이션에서 발생하는 날 것의 예외 정보를 클라이언트에게 그대로 보여주지 않는 것이다
 * 의도된 예외는 철저하게 사전 정의된 오류 코드에 맵핑하여 응답하도록 하며, 의도하지 않은 예외 역시 사전 정의된 시스템 오류 코드를 응답해야 한다
 * 모든 경우에 있어 철저히 로그를 남겨 클라이언트보다 먼저 예외 발생을 인지하는 것은 필수이다
 *
 * Created by KMS on 2021/05/08.
 */
data class ResErrorDTO (
    val code: Int? = null,
    val message: String? = null
)