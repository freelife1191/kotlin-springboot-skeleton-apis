package com.skeleton.common.generator

import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.LocalDateTime

/**
 * Created by KMS on 2021/04/12.
 */
class CodeGeneratorTest {

    @Test
    fun `날짜 계산 테스트`() {
        println("==> 생성된 카테고리코드: ${CodeGenerator.RANDOM()}")
        println(Timestamp.valueOf(LocalDateTime.now().minusYears(20)).time)
        println(Timestamp.valueOf(LocalDateTime.now().minusYears(1)).time)
        println(Timestamp.valueOf(LocalDateTime.now().minusMonths(5).minusHours(1).minusMinutes(1).minusSeconds(1)).time)
        println(System.currentTimeMillis() - ((1000*3600*24)) )
        println(System.currentTimeMillis())
        println(System.currentTimeMillis() + ((1000*3600*24)))
        println(Timestamp.valueOf(LocalDateTime.now().plusMonths(5)).time)
        println(Timestamp.valueOf(LocalDateTime.now().plusYears(1)).time)
        println(Timestamp(System.currentTimeMillis()).toLocalDateTime())
        println(Timestamp(1556175797428).toLocalDateTime())
        println(System.nanoTime())
        println(Timestamp(System.nanoTime()).toLocalDateTime())
    }

    @Test
    fun `Timestamp 자르기`() {
        println(Timestamp.valueOf(LocalDateTime.now()).time)
        println(
            Timestamp.valueOf(LocalDateTime.now()).time.toString().substring(1,10)
        )
    }

    @Test
    fun `랜덤코드 생성기 테스트`() {
        println("==> 생성된 카테고리 코드: ${CodeGenerator.RANDOM("QA")}")
    }

    @Test
    fun `카테코리코드 생성기 테스트`() {
        println("==> 생성된 카테고리 코드: ${CodeGenerator.CATEGORY()}")
    }

    @Test
    fun `주문번호 생성기 테스트`() {
        println("==> 생성된 주문번호: ${CodeGenerator.ORDER()}")
    }
}