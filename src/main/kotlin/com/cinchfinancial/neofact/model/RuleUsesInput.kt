package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.EndNode
import org.neo4j.ogm.annotation.RelationshipEntity
import org.neo4j.ogm.annotation.StartNode

/**
 * Relationship between a [Rule] and [ModelInput] indicating that the [Rule]'s formula uses the given [ModelInput]
 */
@RelationshipEntity(type="USES_INPUT")
class RuleUsesInput() : BaseNode() {

    constructor(rule: Rule, input: ModelInput) : this() {
        this.rule = rule
        this.input = input
    }

    override fun uniqueKey(): String {
        return "${rule.uniqueKey()}:${input.uniqueKey()}"
    }

    @StartNode
    lateinit var rule : Rule

    @EndNode
    lateinit var input: ModelInput
}