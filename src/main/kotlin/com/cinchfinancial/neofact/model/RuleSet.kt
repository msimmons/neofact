package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.GraphId
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

/**
 * A RuleSet is a set of related rules that should be executed in order
 */
@NodeEntity
class RuleSet(var name :String = "") : BaseNode() {

    override fun uniqueKey(): String {
        return name
    }

/*
    @GraphId
    var id : Long? = null
        private set
*/

    @Relationship(type = "HAS_RULE")
    val rules = mutableSetOf<RuleSetHasRule>()

}