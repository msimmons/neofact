package com.cinchfinancial.neofact.repository

import io.kotlintest.specs.BehaviorSpec
import org.springframework.context.annotation.AnnotationConfigApplicationContext

/**
 * Allow autowiring in a kotlin test.  Override this class and supply the list of @Configuration classes that you
 * would like in your spring application context, then we autowire things in the test class
 */
abstract class SpringBehaviorSpec(vararg classes : Class<*>) : BehaviorSpec() {

    val springContext : AnnotationConfigApplicationContext
    init {
        springContext = AnnotationConfigApplicationContext(*classes)
        springContext.autowireCapableBeanFactory.autowireBean(this)
    }

}