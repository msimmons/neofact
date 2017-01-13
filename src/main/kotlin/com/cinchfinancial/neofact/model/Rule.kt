package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.GraphId
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

/**
 * A Rule has a set of RuleStatements (assertions) and a set of Strategies; if all assertions evaluate to true,
 * then the rule is satisfied and the strategies are valid suggestions
 */
@NodeEntity
class Rule(var name: String = "") : BaseNode() {

    override fun uniqueKey(): String {
        return name
    }

/*
    @GraphId
    var id : Long? = null
        private set
*/

    @Relationship(type = "ASSERTS", direction = Relationship.OUTGOING)
    val statements = mutableSetOf<RuleStatement>()

    @Relationship(type="SUGGESTS_STRATEGY")
    val strategies = mutableSetOf<RuleSuggestsStrategy>()

    @Relationship(type="REJECTS_STRATEGY")
    val rejectedStrategies = mutableSetOf<RuleRejectsStrategy>()
}