package com.cinchfinancial.neofact.dsl

/**
 * Created by mark on 3/4/17.
 */
class RulesetNode {

    val rules = mutableListOf<RuleNode>()

    fun rule(init: RuleNode.() -> Unit) : RuleNode {
        val rule = RuleNode()
        rule.init()
        rules += rule
        return rule
    }
}