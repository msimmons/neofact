package com.cinchfinancial.neofact.repository

import com.cinchfinancial.neofact.model.Fact
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.kotlintest.specs.StringSpec

class MockSpec : StringSpec() {

    val myThing : FactRepository = mock() {
        on { count() } doReturn 0L
        (1L..5L).forEach {
            on { findOne(it) } doReturn Fact("aFact$it")
        }
    }

    init {

        "Try using the mock repository" {
            myThing.findOne(5L).name shouldBe "aFact5"
            verify(myThing).findOne(5L)
        }
    }
}