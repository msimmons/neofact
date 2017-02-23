package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

/**
 * A Rule has a set of RuleStatements (assertions) and a set of Strategies; if all assertions evaluate to true,
 * then the rule is satisfied and the outcomes are valid suggestions
 */
@NodeEntity
class Rule(var name: String = "", var formula: String = "true()") : BaseNode() {

    override fun uniqueKey(): String {
        return name
    }

    @Relationship(type="USES_INPUT")
    var inputs = mutableListOf<ModelInput>()
        private set

    @Relationship(type="ASSERTS_OUTCOME")
    var outcomes = mutableSetOf<RuleAssertsOutcome>()
        private set

    @Relationship(type="REJECTS_OUTCOME")
    val rejectedOutcomes = mutableSetOf<RuleRejectsOutcome>()
}