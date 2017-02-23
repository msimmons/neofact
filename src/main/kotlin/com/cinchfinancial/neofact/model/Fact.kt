package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.NodeEntity

/**
 * This node simply represents the name of an known fact.  They are used to calculate [ModelInput]
 */
@NodeEntity
open class Fact(var name: String = "") : BaseNode() {

    override fun uniqueKey(): String {
        return name
    }

}