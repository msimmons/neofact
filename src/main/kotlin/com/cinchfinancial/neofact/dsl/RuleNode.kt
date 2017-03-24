package com.cinchfinancial.neofact.dsl

/**
 * Created by mark on 3/4/17.
 */
class RuleNode {

    private lateinit var formula : ()->Boolean
    val outcomes = mutableListOf<OutcomeNode>()
    var error : Exception? = null
        private set
    val recommend =this

    fun eval(eval: () -> Boolean) {
        formula = eval
    }

    fun evaluate() : Boolean {
        try {
            return formula()
        }
        catch (e:Exception) {
            error = e
        }
        return false
    }

    infix fun outcome(name : String) : OutcomeNode {
        val outcome = OutcomeNode(name)
        outcomes.add(outcome)
        return outcome
    }

    infix fun against(name: String) : OutcomeNode {
        val str = OutcomeNode(name, false)
        outcomes.add(str)
        return str
    }

}