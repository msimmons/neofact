package com.cinchfinancial.neofact.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.neo4j.ogm.typeconversion.AttributeConverter

/**
 * Created by mark on 2/23/17.
 */
class MapConverter : AttributeConverter<Map<String,Any?>, String> {

    val objectMapper = ObjectMapper()

    override fun toEntityAttribute(value: String?): Map<String, Any?> {
        return objectMapper.readValue(value, Map::class.java) as Map<String,Any?>
    }

    override fun toGraphProperty(value: Map<String, Any?>?): String {
        return objectMapper.writeValueAsString(value)
    }
}