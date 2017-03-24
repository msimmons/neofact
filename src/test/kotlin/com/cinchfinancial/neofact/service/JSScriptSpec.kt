package com.cinchfinancial.neofact.repository

import io.kotlintest.specs.BehaviorSpec
import jdk.nashorn.api.scripting.ScriptObjectMirror
import org.springframework.core.io.ClassPathResource
import java.io.InputStreamReader
import javax.script.Invocable
import javax.script.ScriptEngineManager
import kotlin.system.measureTimeMillis

/**
 * Created by mark on 8/22/16.
 */
class JSScriptSpec : BehaviorSpec() {
    init {

        Given("A javascript block") {

            val scriptEngine = ScriptEngineManager().getEngineByName("nashorn")
            val evalTime= measureTimeMillis {
                scriptEngine.eval(InputStreamReader(ClassPathResource("life_model.js").inputStream))
            }
            println(scriptEngine.getBindings(200).keys)
            println("Evaluation: $evalTime")
            println(scriptEngine::class.java)
            val inputFacts = mutableMapOf<String, Any>()
            inputFacts["user_age"] = 25
            inputFacts["user_number_of_children"] = 1
            inputFacts["user_is_homeowner"] = true
            inputFacts["user_is_smoker"] = false
            inputFacts["user_checking_monthly_net_income"] = 6133.00
            inputFacts["user_checking_ending_balance"] = 2000.00
            inputFacts["user_savings_ending_balance"] = 300.00
            inputFacts["user_unlinked_cash"] = 500.00
            inputFacts["user_life_insurance_coverage_amount"] = 0.00
            val facts = scriptEngine["facts"] as MutableMap<String,Any>
            facts.putAll(inputFacts)
            val invoker = scriptEngine as Invocable
            var inputs = invoker.invokeFunction("inputs") as ScriptObjectMirror
            inputs.keys.forEach {
                println("$it = ${inputs[it]}")
            }
            facts["user_age"] = 30
            facts["user_life_insurance_coverage_amount"] = 100000.00
            facts["user_is_smoker"] = true
            inputs = invoker.invokeFunction("inputs") as ScriptObjectMirror
            inputs.keys.forEach {
                println("$it = ${inputs[it]}")
            }

        }
    }

}