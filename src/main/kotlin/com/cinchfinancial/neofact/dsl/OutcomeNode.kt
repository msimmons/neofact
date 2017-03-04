package com.cinchfinancial.neofact.dsl

/**
 * Created by mark on 3/4/17.
 */
class OutcomeNode(val name : String, val recommended : Boolean = true) {

    var reason : String=""
    infix fun because(reason: String) : OutcomeNode {this.reason=reason; return this}

}