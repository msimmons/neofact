package com.cinchfinancial.neofact.repository

import io.kotlintest.specs.BehaviorSpec
import org.junit.Test
import org.junit.internal.runners.JUnit4ClassRunner
import org.junit.runner.RunWith

/**
 * Created by mark on 8/22/16.
 */
@RunWith(JUnit4ClassRunner::class)
class StringTest : BehaviorSpec() {

    val x : Int = 0

    val data = table(
        headers("a", "b", "c"),
        row(1, 2, 3),
        row(4, 5, 6)
    )

    //init {

/*
        "Something tested" {
            forAll(data) { a: Int, b: Int, c: Int ->
                println(a)
                //println(b)
                (a + b + c > 0) shouldBe true
            }
        }
*/

/*
        "Something else" {
        }
*/
//    }

    inline fun foo(
        p1:String = "foo",
        p2:Int = 1
    ){
    }

    @Test
    fun canIRunThis() {
        forAll(data) { a: Int, b: Int, c: Int ->
            println(a)
            //println(b)
            (a + b + c > 0) shouldBe true
        }
    }
}