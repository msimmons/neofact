package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

/**
 * A [ModelInput] is derived from a set of [Fact]s or other [ModelInput]s based on the given formula (in excel expression language)
 */
@NodeEntity
class ModelInput(
    name: String = "",
    var formula: String = "",
    var estimated :Boolean = false,
    var type : ModelInputType = ModelInputType.string
) : Fact(name) {

    @Relationship( type = "DEPENDS_ON", direction = Relationship.OUTGOING)
    var facts = mutableSetOf<Fact>()

}