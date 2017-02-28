package com.cinchfinancial.neofact.service

import java.math.BigDecimal

fun ruleset(init: RULESET.()->Unit) : RULESET {
    val ruleset = RULESET()
    ruleset.init()
    return ruleset
}

fun inputs(init: INPUTS.() ->Unit) : Map<String, INPUT> {
    val inputs = INPUTS()
    inputs.init()
    return inputs.inputs
}

class INPUT(val name: String) {

    lateinit private var formula : () -> Any?

    infix fun formula(formula: () -> Any?) : INPUT {
        this.formula = formula
        return this
    }
}

class INPUTS {

    val inputs = mutableMapOf<String, INPUT>()
    val define =this

    infix fun input(name: String) : INPUT {
        val i = INPUT(name)
        inputs.put(name, i)
        return i
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

class Facts(val facts: Map<String,Any?>) {
    val fact1 : String? by facts
}

fun doit() {

    val facts = Facts(mapOf<String, Any?>())

    val mi = inputs {
        define input "foo" formula {BigDecimal.ZERO}
        define input "bar" formula {facts.fact1 == "helpme"}
    }

    ruleset {
        rule {
            eval {mi["ci.foobar"]==mi["ci.baz"] && false}
            eval {true}
            recommend strategy "something" because "it's good" because "reason2"
            recommend against "other" because "it sucks"
        }
        rule {
        }
    }
}