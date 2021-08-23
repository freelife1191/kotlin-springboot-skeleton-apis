package com.skeleton.common.utils

import java.math.BigDecimal

/**
 * Created by LYT to 2021/04/27
 */
class NumberUtils {

    companion object {
        /**
         * Null -> Return : 0
         * @param param BigDecimal?
         * @return BigDecimal
         */
        fun setZeroIfNull(param: BigDecimal?) = param ?: BigDecimal("0")

        /**
         * BigDecimal Parameters 합계 계산
         * @param params List<BigDecimal?>?
         * @return BigDecimal?
         */
        fun calculateSum(params: List<BigDecimal?>?) =
            params?.sum()

        /**
         * first - second
         *   속성값으로 Null이 넘어오면 Return 0
         * @param payAmount BigDecimal?
         * @param second BigDecimal?
         * @return BigDecimal?
         */
        fun minus(first: BigDecimal?, second: BigDecimal?) =
            first?.minus(setZeroIfNull(second))
                ?: BigDecimal("0")

        private fun Iterable<BigDecimal?>.sum(): BigDecimal {
            var sum = BigDecimal.ZERO
            for (target in this) {
                sum += target ?: BigDecimal.ZERO
            }
            return sum
        }
    }

}