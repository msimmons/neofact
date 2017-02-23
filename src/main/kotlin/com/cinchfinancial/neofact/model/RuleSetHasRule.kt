package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.EndNode
import org.neo4j.ogm.annotation.RelationshipEntity
import org.neo4j.ogm.annotation.StartNode

/**
 * The [RuleSetHasRule] relationship defines the priority rank in which the [Rule]s should be evaluated for a given
 * [RuleSet].
 */
@RelationshipEntity(type="HAS_RULE")
class RuleSetHasRule() : BaseNode() {

    constructor(ruleSet : RuleSet, rule : Rule, rank: Int) : this() {
        this.rank = rank
        this.ruleSet = ruleSet
        this.rule = rule
    }

    override fun uniqueKey(): String {
        return "${ruleSet.uniqueKey()}:${rule.uniqueKey()}"
    }

    var rank: Int = 0

    @StartNode
    lateinit var ruleSet : RuleSet

    @EndNode
    lateinit var rule : Rule
}