package com.skeleton.common.validator.date

import com.skeleton.common.validator.DateValidator
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Created by LYT to 2021/04/22
 */
internal class DateValidatorTest {

    @Test
    fun `시작날짜가 종료날짜보다 빠른 경우 테스트`() {
        // Given
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().plusDays(1)

        // When, Then
        DateValidator.checkOrder(startDate, endDate)
    }

    @Test
    fun `시작날짜가 종료날짜보다 느린 경우 테스트`() {
        // Given
        val startDate = LocalDate.now().plusDays(1)
        val endDate = LocalDate.now()

        // When, Then
        assertThrows(Exception::class.java) {
            DateValidator.checkOrder(startDate, endDate)
        }

    }

    @Test
    fun `날짜 간격 체크 - 일단위`() {
        // Given
        val startDate = LocalDate.now().plusDays(30)
        val endDate = LocalDate.now()

        // When, Then
        DateValidator.checkMaxDayInterval(startDate = startDate, endDate = endDate, diffDays = 30)
    }

    @Test
    fun `날짜 간격 체크 초과 실패 케이스 - 일단위`() {
        // Given
        val startDate = LocalDate.now().plusDays(32)
        val endDate = LocalDate.now()

        // When, Then
        assertThrows(Exception::class.java) {
            DateValidator.checkMaxDayInterval(startDate = startDate, endDate = endDate, diffDays = 30)
        }
    }

    @Test
    fun `날짜 간격 체크 - 월단위`() {
        // Given
        val startDate = LocalDate.now().plusMonths(2)
        val endDate = LocalDate.now()

        // When, Then
        DateValidator.checkMaxMonthInterval(startDate = startDate, endDate = endDate, diffMonths = 2)
    }

    @Test
    fun `날짜 간격 체크 초과 실패 케이스 - 월단위`() {
        // Given
        val startDate = LocalDate.now().plusMonths(2)
        val endDate = LocalDate.now()

        // When, Then
        assertThrows(Exception::class.java) {
            DateValidator.checkMaxMonthInterval(startDate = startDate, endDate = endDate, diffMonths = 1)
        }
    }

    @Test
    fun `날짜 간격 체크 - 년단위`() {
        // Given
        val startDate = LocalDate.now().plusYears(1)
        val endDate = LocalDate.now()

        // When, Then
        DateValidator.checkMaxYearInterval(startDate = startDate, endDate = endDate, diffYears = 2)
    }

    @Test
    fun `날짜 간격 체크 초과 실패 케이스 - 년단위`() {
        // Given
        val startDate = LocalDate.now().plusYears(2)
        val endDate = LocalDate.now()

        // When, Then
        assertThrows(Exception::class.java) {
            DateValidator.checkMaxYearInterval(startDate = startDate, endDate = endDate, diffYears = 1)
        }
    }

}