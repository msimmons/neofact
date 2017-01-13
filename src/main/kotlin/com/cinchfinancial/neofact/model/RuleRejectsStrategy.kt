package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.EndNode
import org.neo4j.ogm.annotation.GraphId
import org.neo4j.ogm.annotation.RelationshipEntity
import org.neo4j.ogm.annotation.StartNode

/**
 * The RuleSet -> RuleSetHasRule relationship defines the sequence in which the rules should be run for a given RuleSet.
 */
@RelationshipEntity(type="REJECTS_STRATEGY")
class RuleRejectsStrategy() : BaseNode() {

    constructor(rule : Rule, strategy: Strategy, reason: String) : this() {
        this.rule = rule
        this.strategy = strategy
        this.reason = reason
    }

    override fun uniqueKey(): String {
        return "${rule.uniqueKey()}:${strategy.uniqueKey()}"
    }

/*
    @GraphId
    var id : Long? = null
        private set
*/

    lateinit var reason : String

    @StartNode
    lateinit var rule : Rule

    @EndNode
    lateinit var strategy : Strategy
}