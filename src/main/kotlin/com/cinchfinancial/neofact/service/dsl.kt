package com.cinchfinancial.neofact.service

import java.math.BigDecimal
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun model(init: MODEL.() -> Unit) : MODEL {
    return MODEL().apply {
        init()
    }
}

class STRATEGY(val name : String) {
    var explanation : String=""
    infix fun because(explanation: String) :STRATEGY {this.explanation=explanation; return this}
}

class RULE {

    val statements = mutableListOf<()->Boolean>()
    val strategies = mutableListOf<STRATEGY>()
    val recommend =this

    fun eval(eval: () -> Boolean) {
        statements.add(eval)
    }

    fun evaluate() : Boolean {
        return statements.any { it.invoke() }
    }

    infix fun strategy(name : String) : STRATEGY {
        val str = STRATEGY(name)
        strategies.add(str)
        return str
    }

    infix fun against(name: String) : STRATEGY {
        val str = STRATEGY(name)
        strategies.add(str)
        return str
    }
}

class RULESET {

    val rules = mutableListOf<RULE>()

    fun rule(init: RULE.() -> Unit) :RULE {
        val rule = RULE()
        rule.init()
        rules += rule
        return rule
    }
}

class InputDelegate<T, V>(val formula: () -> Any?) {

    lateinit var name : String

    operator fun provideDelegate(thisRef: T, prop: KProperty<*>) : ReadOnlyProperty<T, V> {
        name = prop.name
        return object : ReadOnlyProperty<T,V> {
            override fun getValue(thisRef: T, property: KProperty<*>): V {
                return formula() as V
            }
        }
    }
}

class MODEL {

    val facts = mutableMapOf<String, Any?>()
    val rulesets = mutableListOf<RULESET>()
    private val inputList = mutableSetOf<InputDelegate<*,*>>()

    fun ruleset(init: RULESET.() ->Unit) : RULESET {
        return RULESET().apply {
            init()
            rulesets.add(this)
        }
    }

    fun <T, V> T.formula(formula: () -> Any?) : InputDelegate<T, V> {
        return InputDelegate<T, V>(formula).apply { inputList.add(this) }
    }

    fun inputs() : Map<String, Any?> {
        return inputList.associate { Pair(it.name, it.formula()) }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            val theModel = model {

                // Facts
                facts.putAll(mapOf("a_fact_name" to "a_fact_value", "another_fact" to listOf(BigDecimal.TEN, BigDecimal.ONE)))
                val a_fact_name : String by facts
                val another_fact : List<BigDecimal> by facts

                // Inputs
                val an_input : String by formula {a_fact_name}
                val another_input : BigDecimal by formula { another_fact[0] }
                val yet_another : BigDecimal by formula { another_fact.reduce { acc, bigDecimal -> acc+bigDecimal } }
                val fancy_one : Boolean by formula {
                    when(an_input) {
                        null -> false
                        else -> true
                    }
                }

                // Rules
                ruleset {
                    rule {
                        eval {  another_input == another_fact[1] }
                    }
                    rule {
                        eval {  another_fact.size > 0 }
                    }
                }

            }
            println(theModel.inputs())
            theModel.rulesets.forEach { it.rules.forEach { println(it.evaluate()) } }
        }
    }
}