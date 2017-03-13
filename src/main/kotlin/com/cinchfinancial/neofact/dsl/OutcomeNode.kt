package com.cinchfinancial.neofact.dsl

/**
 * Created by mark on 3/4/17.
 */
class OutcomeNode(val name : String, val recommended : Boolean = true) {

    private val attributes = mutableMapOf<String,Any>()

    var reason : String=""

    infix fun because(reason: String) : OutcomeNode {
        this.reason=reason;
        return this
    }

    infix fun attribute(pair: Pair<String,Any>) : OutcomeNode {
        this[pair.first] = pair.second
        return this
    }

    infix fun options(map: Map<String,Any>) : OutcomeNode {
        attributes.putAll(map)
        return this
    }

    operator fun get(key: String) = attributes[key]
    operator fun set(key: String, value: Any) { attributes[key] = value }
    operator fun invoke(key: String, value: Any) {attributes[key] = value }
}