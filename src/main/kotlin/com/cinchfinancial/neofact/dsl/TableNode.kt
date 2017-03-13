package com.cinchfinancial.neofact.dsl

/**
 * Created by mark on 3/4/17.
 */
class TableNode() {

    val data = mutableListOf<List<Any>>()

    fun row(vararg columns : Any) {
        data.add(columns.asList())
    }

}