package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.EndNode
import org.neo4j.ogm.annotation.RelationshipEntity
import org.neo4j.ogm.annotation.StartNode

/**
 * The RuleSet -> RuleSetHasRule relationship defines the sequence in which the rules should be run for a given RuleSet.
 */
@RelationshipEntity(type="DEPENDS_ON")
class InputDependsOnFact() : BaseNode() {

    constructor(modelInput: ModelInput, fact: Fact) : this() {
        this.modelInput = modelInput
        this.fact = fact
    }

    override fun uniqueKey(): String {
        return "${modelInput.uniqueKey()}:${fact.uniqueKey()}"
    }

/*
    @GraphId
    var id : Long? = null
        private set
*/

    @StartNode
    lateinit var modelInput : ModelInput

    @EndNode
    lateinit var fact : Fact
}