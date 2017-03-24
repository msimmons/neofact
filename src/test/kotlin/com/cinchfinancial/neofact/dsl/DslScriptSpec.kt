package com.cinchfinancial.neofact.dsl

import io.kotlintest.specs.BehaviorSpec
import org.springframework.core.io.ClassPathResource
import java.io.InputStreamReader
import java.math.BigDecimal
import javax.script.ScriptEngineManager
import kotlin.system.measureTimeMillis

/**
 * Created by mark on 3/4/17.
 */
class DslScriptSpec : BehaviorSpec() {

    init {

/*
        Given("A model as a kts script") {
            val factory = ScriptEngineManager().getEngineByExtension("kts").factory
            val engine = factory.scriptEngine
            val model = engine.eval(InputStreamReader(ClassPathResource("dsl_model.kts").inputStream)) as ModelNode
            model.facts.putAll(mapOf("a_fact_name" to "a_fact_value", "another_fact" to listOf(BigDecimal.TEN, BigDecimal.ONE)))
            println("${model.inputs()}")
            println("${model.rules}")
            println("${model.missingFacts}")
        }
*/

        Given("A model as a kts script") {
            val factory = ScriptEngineManager().getEngineByExtension("kts").factory
            val engine = factory.scriptEngine
            var model = ModelNode()
            val evalTime= measureTimeMillis {
                model = engine.eval(InputStreamReader(ClassPathResource("dsl_model.kts").inputStream)) as ModelNode
            }
            val facts = mutableMapOf<String, Any>()
            facts["user_age"] = 25
            facts["user_number_of_children"] = 1
            facts["user_is_homeowner"] = true
            facts["user_is_smoker"] = false
            facts["user_checking_monthly_net_income"] = 6133.00
            facts["user_checking_ending_balance"] = 2000.00
            facts["user_savings_ending_balance"] = 300.00
            facts["user_unlinked_cash"] = 500.00
            facts["user_life_insurance_coverage_amount"] = 0.00
            model.facts.putAll(facts)
            model.facts.putAll(mapOf("a_fact_name" to "a_fact_value", "another_fact" to listOf(BigDecimal.TEN, BigDecimal.ONE)))
            println(model.facts)
            val elapsed = measureTimeMillis {
                println("${model.inputs()}")
            }
            println("Eval = $evalTime")
            println("Elapsed = $elapsed")
            model.rules.forEach {
                print(it.evaluate())
                it.outcomes.forEach {
                    println(" ${it.options()}")
                }
            }
            println("${model.missingFacts}")
        }
    }

}
