package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.NodeEntity

/**
 * A recommends is the result of a successful rule assertion
 */
@NodeEntity
class Strategy : BaseNode {

    constructor(name: String) {
        this.name = name
    }

    override fun uniqueKey(): String {
        return name
    }

/*
    @GraphId
    var id : Long? = null
        private set
*/

    lateinit var name : String

}