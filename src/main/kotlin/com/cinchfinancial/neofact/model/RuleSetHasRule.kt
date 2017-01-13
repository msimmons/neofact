package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.EndNode
import org.neo4j.ogm.annotation.RelationshipEntity
import org.neo4j.ogm.annotation.StartNode

/**
 * The RuleSet -> RuleSetHasRule relationship defines the sequence in which the rules should be run for a given RuleSet.
 */
@RelationshipEntity(type="HAS_RULE")
class RuleSetHasRule() : BaseNode() {

    constructor(ruleSet : RuleSet, rule : Rule, sequence: Int) : this() {
        this.sequence = sequence
        this.ruleSet = ruleSet
        this.rule = rule
    }

    override fun uniqueKey(): String {
        return "${ruleSet.uniqueKey()}:${rule.uniqueKey()}"
    }

/*
    @GraphId
    var id : Long? = null
        private set
*/

    var sequence : Int = 0

    @StartNode
    lateinit var ruleSet : RuleSet

    @EndNode
    lateinit var rule : Rule
}