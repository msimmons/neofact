package com.cinchfinancial.neofact.repository

import io.kotlintest.specs.StringSpec
import java.math.BigDecimal
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * For handling properties that are themselves maps, this returns an object that delegates to the sub map as well
 */
fun <T, TValue> T.subMap(properties: Map<String, Any?>, createIt: (Map<String,Any?>) -> TValue): ReadOnlyProperty<T, TValue> {
    return object : ReadOnlyProperty<T, TValue> {
        override fun getValue(thisRef: T, property: KProperty<*>) : TValue {
            //val subMap : Map<String,Any?> = properties.getOrDefault(property.name, mapOf<String,Any?>()) as Map<String,Any?>
            val subMap : Map<String,Any?> = properties.get(property.name) as Map<String,Any?>
            return createIt(subMap)
        }
    }
}

/**
 * Created by mark on 8/22/16.
 */
class DelegateTest : StringSpec() {

    init {

        "Test map delegation" {
            val subMap = mapOf<String,Any?>("sub1" to 3, "sub2" to true)
            val facts = mapOf<String,Any?>("credit_balance" to BigDecimal("10.30"), "sub" to subMap)
            val factObject = FactObj(facts)
            factObject.credit_balance.compareTo(BigDecimal.valueOf(10.3)) shouldBe 0
/*
            factObject.sub.sub1 shouldBe 3
            factObject.sub.sub2 shouldBe true
            factObject.sub.sub3 shouldBe null
            factObject.sub.sub4 shouldEqual "hello"
*/
        }

    }

    class FactObj(map : Map<String,Any?>) {
        val credit_balance: BigDecimal by map
        //val sub : SubObj by subMap(map, ::SubObj)

        class SubObj(data: Map<String, Any?>) {
            val sub1: Int? by data
            val sub2: Boolean? by data
            val sub3: String? by data.withDefault { null }
            val sub4: String by data.withDefault { "hello" }
        }
    }
}