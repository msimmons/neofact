package com.cinchfinancial.neofact.model

import com.sun.xml.internal.fastinfoset.util.StringArray
import org.neo4j.ogm.annotation.EndNode
import org.neo4j.ogm.annotation.RelationshipEntity
import org.neo4j.ogm.annotation.StartNode

/**
 * [RuleAssertsOutcome] indicates which [Outcome]s are asserted for a successful evaluation of the [Rule].  The assertion
 * can also have a set of reasons describing why the [Outcome] was asserted by the [Rule]
 */
@RelationshipEntity(type="ASSERTS_OUTCOME")
class RuleAssertsOutcome() : BaseNode() {

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