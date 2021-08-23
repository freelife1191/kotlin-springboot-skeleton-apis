package com.skeleton.common.utils

import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaField

/**
 * Created by KMS on 2021/04/07.
 */
class EntityUtils {
    companion object {
        /**
         * changeFields 만들기
         * 테스트코드에서 Class 호출시 생성
         */
        /**
         * changeFields 만들기
         * 테스트코드에서 Class 호출시 생성
         */
        fun makeChangeFields(entity: KClass<*>) {

            val exceptList: MutableList<String> = mutableListOf(
                "MutableList",
            )

            println(exceptList.contains("MutableList.data"))

            val constructors = entity.constructors // 모든 생성자 정보
            val memberProperty = entity.memberProperties.toMutableList() // 주 생성자

            println()
            println()
            val lowerEntityName = entity.simpleName.toString().toLowerCase()
            println("fun changeFieldsByEntity(${lowerEntityName}: ${entity.simpleName}): ${entity.simpleName} {")
            for (constructor in constructors){
                val parameters = constructor.parameters
                parameters@ for (parameter in parameters) {

                    // 상위 클래스 일부 프로퍼티와 와 중복 프로퍼티 삭제
                    memberProperty.removeIf {
                        it.name == "createdAt"
                            || it.name == "updatedAt"
                            || it.name == parameter.name
                    }

                    for (exceptStr in exceptList) {
                        if(exceptStr in parameter.type.toString())
                            continue@parameters
                    }

                    println("    if(${lowerEntityName}.${parameter.name} != null) this.${parameter.name} = ${lowerEntityName}.${parameter.name}")
                }
            }

            for (property in memberProperty) {
                println("    if(${lowerEntityName}.${property.name} != null) this.${property.name} = ${lowerEntityName}.${property.name}")
            }

            println("    return this")
            println("}")
            println()
            println("fun changeFields(")
            for (constructor in constructors) {
                val parameters = constructor.parameters
                parameters@ for (parameter in parameters) {
                    for (exceptStr in exceptList) {
                        if(exceptStr in parameter.type.toString())
                            continue@parameters
                    }
                    val lastIndex = parameter.type.toString().split(".").lastIndex
                    println(
                        "    ${parameter.name}: ${parameter.type.toString().replace("?","").split(".")[lastIndex]}? = null,"
                    )
                }
            }

            for (property in memberProperty) {
                val propertyType = property.javaField?.type.toString()
                val lastIndex = propertyType.split(".").lastIndex

                println(
                    "    ${property.name}: ${propertyType.replace("?", "").split(".")[lastIndex]}? = null,"
                )
            }

            println("): ${entity.simpleName} {")
            for (constructor in constructors) {
                val parameters = constructor.parameters
                parameters@ for (parameter in parameters) {
                    for (exceptStr in exceptList) {
                        if(exceptStr in parameter.type.toString())
                            continue@parameters
                    }
                    println("    if(${parameter.name} != null) this.${parameter.name} = ${parameter.name}")
                }
            }

            for (property in memberProperty) {
                println("    if(${property.name} != null) this.${property.name} = ${property.name}")
            }

            println("    return this")
            println("}")
            println()
            println()
        }

        /**
         * MongoDB 객체 Predicate 생성기
         * @param entity KClass<*>
         * @param dtoName String
         * @param el String
         */
        fun makeMongoPredicate(entity: KClass<*>, dtoName: String = "req", el: String = "eq") {
            val entityName = entity.simpleName?.lowercase()
            println("val predicate = BooleanBuilder()")
            val constructors = entity.constructors // 모든 생성자 정보
            for (constructor in constructors) {
                constructor.parameters.forEach {
                    println("if($dtoName.${it.name} != null) predicate.and($entityName.${it.name}.$el($dtoName.${it.name}))")
                }
            }
        }
    }
}