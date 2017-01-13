package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.Relationship

/**
 * A ModelInput is derived from a set of Facts or other ModelInputs based on the given formula (in excel expression language)
 */
class ModelInput : Fact {

    constructor(name : String, formula : String) : super(name) {
        this.formula = formula
    }

    @Relationship(type = "DEPENDS_ON")
    val facts = mutableSetOf<Fact>()

    lateinit var formula : String

}