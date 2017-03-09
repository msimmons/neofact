package com.cinchfinancial.neofact.dsl

/**
 * Created by mark on 3/4/17.
 */
class ModelNode {

    val missingFacts = mutableSetOf<String>()
    val facts = mutableMapOf<String, Any?>().withDefault { key->
        missingFacts.add(key)
        null
    }
    private val inputSet = mutableSetOf<InputDelegate<*, *>>()
    val rules = mutableListOf<RuleNode>()

    fun rule(init: RuleNode.() ->Unit) : RuleNode {
        return RuleNode().apply {
            init()
            rules.add(this)
        }
    }

    fun <T, V> T.formula(formula: () -> V) : InputDelegate<T, V> {
        return InputDelegate<T, V>(formula).apply { inputSet.add(this) }
    }

    fun inputs() : Map<String, Any?> {
        return inputSet.associate {
            val value = try{it.formula()} catch (e:Exception) {e.message}
            Pair(it.name, value)
        }
    }

    fun table(init: TableNode.()->Unit) : TableNode {
        return TableNode().apply {
            init()
        }
    }
}

fun model(init: ModelNode.() -> Unit) : ModelNode {
    return ModelNode().apply {
        init()
    }
}