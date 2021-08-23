package com.skeleton.config

import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestConstructor

/**
 * Created by LYT to 2021/04/05
 */
@DataJpaTest
@Import(TestJpaConfig::class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
annotation class BaseJpaTest()
