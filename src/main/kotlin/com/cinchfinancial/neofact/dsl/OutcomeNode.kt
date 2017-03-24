package com.cinchfinancial.neofact.dsl

/**
 * Created by mark on 3/4/17.
 */
class OutcomeNode(val name : String, val recommended : Boolean = true) {

    private val optionMap = mutableMapOf<String,()->Any?>()

    var reason : String=""

    infix fun because(reason: String) : OutcomeNode {
        this.reason=reason;
        return this
    }

    infix fun options(setOptions: OutcomeNode.()-> Unit) {
        this.apply { setOptions() }
    }
    operator infix fun String.invoke(formula: ()->Any?) {optionMap.put(this, formula)}

    fun options() : Map<String, Any?> {
        return optionMap.mapValues { it.value() }
    }
}