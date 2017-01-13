package com.cinchfinancial.neofact.model

import org.neo4j.ogm.annotation.GraphId

/**
 * Created by mark on 11/1/16.
 */
abstract class BaseNode {

    @GraphId
    var id : Long? = null
        private set

    abstract fun uniqueKey() : String

    override fun toString() : String {
        return uniqueKey()
    }

    override fun equals(other : Any?) : Boolean {
        when (other) {
            null -> return false
            is BaseNode -> return javaClass==other.javaClass && uniqueKey()==other.uniqueKey()
            else -> return false
        }
    }

    override fun hashCode() : Int {
        return uniqueKey().hashCode()
    }
}