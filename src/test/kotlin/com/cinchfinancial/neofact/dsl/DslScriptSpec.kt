package com.cinchfinancial.neofact.dsl

import io.kotlintest.specs.BehaviorSpec
import org.springframework.core.io.ClassPathResource
import java.io.InputStreamReader
import java.math.BigDecimal
import javax.script.ScriptEngineManager

/**
 * Created by mark on 3/4/17.
 */
class DslScriptSpec : BehaviorSpec() {

    init {

        Given("A model as a kts script") {
            val factory = ScriptEngineManager().getEngineByExtension("kts").factory
            val engine = factory.scriptEngine
            val model = engine.eval(InputStreamReader(ClassPathResource("dsl_model.kts").inputStream)) as ModelNode
            model.facts.putAll(mapOf("a_fact_name" to "a_fact_value", "another_fact" to listOf(BigDecimal.TEN, BigDecimal.ONE)))
            println("${model.inputs()}")
            println("${model.rules}")
            println("${model.missingFacts}")
        }
    }

}
