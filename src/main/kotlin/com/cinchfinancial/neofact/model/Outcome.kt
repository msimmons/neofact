package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.typeconversion.Convert

/**
 * A [Outcome] is the result of evaluating a rule
 * [Outcomes]s can have custom properties
 */
@NodeEntity
class Outcome(var name: String = "") : BaseNode() {

    override fun uniqueKey(): String {
        return name
    }

    @Convert(MapConverter::class)
    var properties = mutableMapOf<String,Any?>()
        private set

    operator fun get(key: String?) : Any? = properties[key]
    operator fun set(key: String, value: Any?) : Unit {properties[key] = value}

}