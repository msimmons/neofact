package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.EndNode
import org.neo4j.ogm.annotation.RelationshipEntity
import org.neo4j.ogm.annotation.StartNode

/**
 * Relationship between a RuleStatement and ModelInput asserting that the eval uses the input in its
 * calculations
 */
@RelationshipEntity(type="USES_INPUT")
class RuleStatementUsesInput() : BaseNode() {

    constructor(statement: RuleStatement, input: ModelInput) : this() {
        this.statement = statement
        this.input = input
    }

    override fun uniqueKey(): String {
        return "${statement.uniqueKey()}:${input.uniqueKey()}"
    }

    @StartNode
    lateinit var statement : RuleStatement

    @EndNode
    lateinit var input: ModelInput
}