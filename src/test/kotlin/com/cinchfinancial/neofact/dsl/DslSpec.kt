package com.cinchfinancial.neofact.dsl

import io.kotlintest.specs.BehaviorSpec
import java.math.BigDecimal

/**
 * Created by mark on 3/4/17.
 */
class DslSpec : BehaviorSpec() {

    init {

        Given("A model") {

            val theModel = model {
                // Facts
                val a_fact_name: String by facts
                val another_fact: List<BigDecimal> by facts

                // Inputs
                val an_input by formula { a_fact_name }
                val another_input by formula { another_fact[0] }
                val yet_another by formula { another_fact.reduce { acc, bigDecimal -> acc + bigDecimal } }
                val fancy_one by formula {
                    when (an_input) {
                        null -> false
                        else -> true
                    }
                }

                // Rules
                rule {
                    eval { another_input == another_fact[1] }
                    recommend outcome "MYOUT" because "ITHURTS"
                    recommend against "ANOTHER" because "WHYNOT"
                }
                rule {
                    eval { another_fact.size > 2 }
                    recommend outcome "MYOUT" because "DIFFERENT"
                }

                rule {
                    eval { fancy_one && yet_another > BigDecimal.ZERO }
                    recommend outcome "FOOEY" because "WOWY"
                }

                rule {
                    eval { println(an_input); an_input?.length ?: 0 > 2 }
                    recommend outcome "FOOEY" because "WOWY"
                }

            }
            Then("It has the properties and stuff") {
                theModel.facts.putAll(mapOf("a_fact_name" to "a_fact_value", "another_fact" to listOf(BigDecimal.TEN, BigDecimal.ONE)))
                theModel.inputs().size shouldEqual 4
                theModel.rules.firstOrNull { it.evaluate() }?.outcomes?.forEach {
                    println("${it.name} ${it.reason} ${it.recommended}")
                }
            }

            Then("Change the facts") {
                theModel.facts.putAll(mapOf("a_fact_name" to null, "another_fact" to listOf(BigDecimal.TEN)))
                theModel.rules.firstOrNull { it.evaluate() }?.outcomes?.forEach {
                    println("${it.name} ${it.reason} ${it.recommended}")
                }
                theModel.rules.forEach { it.evaluate(); println(it.error) }
            }
        }
    }
}
