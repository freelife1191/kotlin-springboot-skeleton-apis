package com.skeleton.common.generator

import org.apache.commons.lang3.RandomStringUtils
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 코드 생성기
 * Created by KMS on 2021/04/12.
 */
class CodeGenerator {
    companion object {

        /**
         * 랜덤코드 생성
         * 요구사항 : 총 13자리
         * 요구사항 : prefix = OP + 숫자9(밀리세컨드 둘째자리부터 9자리) + BC(숫자 + 대소문자 완전랜덤)
         * 예상코드1 : QA 620618361 BC
         *
         * SP - 상품코드(Product)
         * PI - 품목코드(Item)
         * SH - 배송코드(Shipping)
         * SG - 묶음배송코드(ShippingGroup)
         */
        fun RANDOM(code: String="", datetime: LocalDateTime = LocalDateTime.now()): String {
            if(code.length > 2)
                throw IllegalArgumentException("code의 자리수는 2를 초과할 수 없습니다")

            // 밀리세컨드 뒷자리(7)
            val currentTime = Timestamp.valueOf(datetime).time.toString().substring(1,10)
            val lastCode = RandomStringUtils.randomAlphanumeric(2)
            return "$code$currentTime$lastCode"
        }

        /**
         * 주문번호 생성
         * 년월일 = 20210409
         * 시분초 = 시간(23시)+분초(59분30초) = 2300+(59*60)+30 = 5899
         * 밀리세컨드 앞네자리 = 8709
         *
         * 년월일시분초(12)+ 완전랜덤 랜덤대소문자숫자(6) 총 18
         * 예상코드 21 04 01 11 35 59 +ABC983
         */
        fun ORDER(datetime: LocalDateTime = LocalDateTime.now()) : String {
            val firstCode = datetime.format(DateTimeFormatter.ofPattern("yyMMddHHmmss"))
            val lastCode = RandomStringUtils.randomAlphanumeric(6)
            return firstCode + lastCode
        }

        /**
         * 카테고리코드 생성
         * 요구사항 : 총 9자리
         * 생성방법 : A-Z 두자리 영문을 3분단위로 조합문자(2)+밀리세컨드뒷자리(7)
         * 예상코드 : JR7726302
         */
        fun CATEGORY(datetime: LocalDateTime = LocalDateTime.now()): String {
            val dataMap: MutableMap<String, String> = mutableMapOf()
            var hour = 0
            var min = 0
            for (c in 'A'..'Z') {
                for (d in 'A'..'Z') {
                    if (min >= 60) {
                        min = 0
                        hour += 1
                    }
                    for (plus in 0..2)
                        dataMap["%02d".format(hour) + "%02d".format(min + plus)] = c.toString() + d.toString()
                    if (hour == 23 && min >= 57)
                        break
                    min += 3
                }
            }

            val firstCode = dataMap[datetime.format(DateTimeFormatter.ofPattern("HHmm"))]

            // 밀리세컨드 뒷자리(7)
            val currentTime = System.currentTimeMillis().toString()
            val lastCode = currentTime.substring(currentTime.length-7, currentTime.length)
            return firstCode + lastCode

        }
    }
}