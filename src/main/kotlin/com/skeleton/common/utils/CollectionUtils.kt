package com.skeleton.common.utils

import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function
import java.util.function.Predicate

import java.util.stream.Collectors

/**
 * 컬렉션 유틸
 * Created by KMS on 2021/04/28.
 */
class CollectionUtils {
    companion object {
        /**
         * 리스트의 특정 값의 중복 여부 확인후 중복 객체 제거
         * 사용 예시) CollectionUtils.distinct(optionKeyList, ResOptionSearchDTO::id)
         * @param list 중복이 있는 list
         * @param key  중복 여부를 판단하는 키값
         * @param <T>  generic type
         * @return list
        </T> */
        fun <T : Any> distinct(list: List<T>, key: Function<in T, *>): List<T> {
            return list.stream()
                .filter(distinct(key))
                .collect(Collectors.toList())
        }

        private fun <T : Any> distinct(key: Function<in T, *>): Predicate<in T> {
            val set: MutableSet<Any> = ConcurrentHashMap.newKeySet()
            return Predicate<T> { predicate -> set.add(key.apply(predicate)) }
        }

        /**
         * 컬렉션 조합
         * @param answer MutableList<List<T>>
         * @param el List<T>
         * @param ck Array<Boolean>
         * @param start Int
         * @param target Int
         */
        /*
        fun <T, E, O: Any> combination(list1: List<T>, list2: List<E>, clz: KClass<O>):List<O> {

        }
        */
    }
}