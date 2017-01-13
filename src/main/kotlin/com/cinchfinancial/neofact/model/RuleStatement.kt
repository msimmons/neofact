package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

/**
 * A RuleStatement evaluates it's formula against the given set of ModelInputs
 */
@NodeEntity
class RuleStatement(var formula: String = "") : BaseNode() {

    override fun uniqueKey(): String {
        return id?.toString() ?: ""
    }

/*
    @GraphId
    var id : Long? = null
        private set
*/

    @Relationship(type = "USES_INPUT")
    val inputs = mutableSetOf<ModelInput>()

}