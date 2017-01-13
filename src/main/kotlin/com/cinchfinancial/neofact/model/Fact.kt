package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.NodeEntity

/**
 * This node simply represents the name of an known fact.  They are used as inputs to rules directly or to calculate
 * other rule inputs
 */
@NodeEntity
open class Fact : BaseNode {

    override fun uniqueKey(): String {
        return name
    }

    constructor(name: String) {
        this.name = name
    }

/*
    @GraphId
    var id : Long? = null
        private set
*/

    lateinit var name : String

}