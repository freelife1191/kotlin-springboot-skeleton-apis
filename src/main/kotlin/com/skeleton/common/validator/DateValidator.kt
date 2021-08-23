package com.skeleton.common.validator

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Created by LYT to 2021/04/21
 */
class DateValidator {

    companion object {
        /**
         * 시작날짜는 종료날짜보다 작은지 체크
         */
        fun checkOrder(startDate: LocalDate?, endDate: LocalDate?) {
            if (startDate?.isAfter(endDate) == true) {
                throw Exception()
            }
        }

        /**
         * 시작날짜와 종료날짜 Interval 체크 ( Day 단위 )
         */
        fun checkMaxDayInterval(startDate: LocalDate?, endDate: LocalDate?, diffDays: Long) {
            startDate ?: return
            endDate ?: return

            if (ChronoUnit.DAYS.between(endDate, startDate) > diffDays) {
                throw Exception()
            }
        }

        /**
         * 시작날짜와 종료날짜 Interval 체크 ( Month 단위 )
         */
        fun checkMaxMonthInterval(startDate: LocalDate?, endDate: LocalDate?, diffMonths: Long) {
            startDate ?: return
            endDate ?: return

            if (ChronoUnit.MONTHS.between(endDate, startDate) > diffMonths) {
                throw Exception()
            }
        }

        /**
         * 시작날짜와 종료날짜 Interval 체크 ( Year 단위 )
         */
        fun checkMaxYearInterval(startDate: LocalDate?, endDate: LocalDate?, diffYears: Long) {
            startDate ?: return
            endDate ?: return

            val between = ChronoUnit.YEARS.between(endDate, startDate)
            if (between > diffYears) {
                throw Exception()
            }
        }
    }
}