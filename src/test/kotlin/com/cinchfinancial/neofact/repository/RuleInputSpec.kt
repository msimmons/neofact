package com.cinchfinancial.neofact.repository

import com.cinchfinancial.neofact.model.Fact
import com.cinchfinancial.neofact.model.ModelInput
import io.kotlintest.matchers.be
import io.kotlintest.specs.BehaviorSpec
import org.apache.poi.ss.formula.WorkbookEvaluator
import java.math.BigDecimal

/**
 * Created by mark on 8/25/16.
 */
class RuleInputSpec : BehaviorSpec() {

    init {

        Given("Some facts and an input with a formula") {
            println(WorkbookEvaluator.getSupportedFunctionNames())

            val fact1 = Fact("pv")
            val fact2 = Fact("rate")
            val fact3 = Fact("term")
            val fact4 = Fact("nper")
            val formula = "FV(rate/nper, term*nper, 0, -pv)"
            val input = ModelInput("myInput", "FV(rate/nper, term*nper, 0, -pv)")
            input.facts.addAll(setOf<Fact>(fact1, fact2, fact3, fact4))
            val values = mapOf<String, Any>("rate" to 0.0500, "term" to 10.0, "nper" to 12.0, "pv" to -1000.0)

            When("Evaluating the formula") {
                val result = BigDecimal(-1647)

                Then("It should produce the right answers") {
                    result should be a BigDecimal::class
                    result as BigDecimal shouldBe (-1647.00 plusOrMinus 1.00)
                    println(result)
                }

            }

        }
    }
}