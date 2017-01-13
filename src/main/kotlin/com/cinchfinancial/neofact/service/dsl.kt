package com.cinchfinancial.neofact.service

import java.math.BigDecimal

fun ruleset(init: RULESET.()->Unit) : RULESET {
    val ruleset = RULESET()
    ruleset.init()
    return ruleset
}

fun inputs(init: INPUTS.() ->Unit) : INPUTS {
    val inputs = INPUTS()
    inputs.init()
    return inputs
}

class INPUT(val name: String) {

    lateinit private var formula : () -> Any?

    infix fun formula(formula: () -> Any?) : INPUT {
        this.formula = formula
        return this
    }
}

class INPUTS {
    val inputs = mutableListOf<INPUT>()

    val declare=this

    operator fun invoke(name: String) : INPUT {
        val input = INPUT(name)
        return input
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

fun doit() {
    val inputs = mapOf<String, Any?>()
    inputs {
        declare("foo") formula {BigDecimal.ZERO}
    }

    ruleset {
        rule {
            eval {inputs["ci.foobar"]==inputs["ci.baz"] && false}
            eval {true}
            recommend strategy "something" because "it's good" because "reason2"
            recommend against "other" because "it sucks"
        }
        rule {
        }
    }
}