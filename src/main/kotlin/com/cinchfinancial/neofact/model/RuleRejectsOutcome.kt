package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.EndNode
import org.neo4j.ogm.annotation.RelationshipEntity
import org.neo4j.ogm.annotation.StartNode

/**
 * A [Rule] can explicitly state which [Outcome]'s it rejects through this relationship.  In addition, it can describe
 * reasons why the [Outcome] is rejected
 */
@RelationshipEntity(type="REJECTS_OUTCOME")
class RuleRejectsOutcome() : BaseNode() {

    constructor(rule : Rule, outcome: Outcome, reasons: Array<String>) : this() {
        this.rule = rule
        this.outcome = outcome
        this.reasons.addAll(reasons)
    }

    override fun uniqueKey(): String {
        return "${rule.uniqueKey()}:${outcome.uniqueKey()}"
    }

    var reasons = mutableSetOf<String>()
        private set

    @StartNode
    lateinit var rule : Rule

    @EndNode
    lateinit var outcome: Outcome
}