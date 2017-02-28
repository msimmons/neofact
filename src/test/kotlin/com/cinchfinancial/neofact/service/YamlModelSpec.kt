package com.cinchfinancial.neofact.service

import com.cinchfinancial.neofact.model.ModelInput
import io.kotlintest.specs.BehaviorSpec
import org.springframework.core.io.ClassPathResource
import org.yaml.snakeyaml.Yaml
import java.math.BigDecimal

/**
 * Created by mark on 1/12/17.
 */
class YamlModelSpec : BehaviorSpec() {

    val keywords = setOf("and", "if", "sum", "max", "min", "or")

    init {
        val yaml = Yaml().load(ClassPathResource("life_model.yml").inputStream) as Map<String, out Any>
        val inputs = yaml["inputs"] as Collection<Map<String, out Any>>
        val tables =  (yaml["tables"] ?: mapOf<String, List<List<out Any>>>()) as Map<String, List<List<out Any>>>

        // Create some model inputs
        val facts = mutableMapOf<String, Any?>()
        val modelInputs = createModelInputs(inputs)

        facts["user.age"] = 31
        facts["user.number_of_children"] = 1
        facts["user.is_homeowner"] = true
        facts["user.is_smoker"] = false
        facts["user.checking.monthly_net_income"] = BigDecimal.valueOf(6133)
        facts["user.checking.ending_balance"] = BigDecimal.valueOf(2000)
        facts["user.savings.ending_balance"] = BigDecimal.valueOf(300)
        facts["user.unlinked_cash"] = BigDecimal.valueOf(500)
        facts["user.life_insurance.coverage_amount"] = 10000

        println("Memory Used: ${Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()}")
        val evaluator = ModelEvaluator(facts.keys, modelInputs.values, tables)

        var inputValues: Map<String, Any?> = mapOf()
        inputValues = evaluator.evaluateInputs(facts)
        inputValues.forEach { s, any -> println("$s: $any") }
        println("Average Evaluation time: ${evaluator.evaluationTime / evaluator.evaluationCount}")
        println("Average Collection time: ${evaluator.collectionTime / evaluator.evaluationCount}")

        // Do it again with some different fact values, especially the collection
        facts["user.is_smoker"] = true
        facts["user.unlinked_cash"] = BigDecimal.valueOf(1000)

        inputValues = evaluator.evaluateInputs(facts)
        inputValues = evaluator.evaluateInputs(facts)

        inputValues.forEach { s, any -> println("$s: $any") }

        println("Setup time: ${evaluator.setupTime}")
        println("Average Evaluation time: ${evaluator.evaluationTime / evaluator.evaluationCount}")
        println("Average Collection time: ${evaluator.collectionTime / evaluator.evaluationCount}")
        println("Memory Used: ${Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()}")

        evaluator.writeToFile("yamlone.xlsx")
    }

    private fun createModelInputs(inputs: Collection<Map<String,out Any>>): Map<String, ModelInput> {
        val modelInputs = mutableMapOf<String, ModelInput>()
        inputs.forEach {
            val input = ModelInput(it["Name"].toString(), it["Formula"].toString())
            modelInputs[input.name] = input
            println("Creating model input ${input.name}")
        }
        return modelInputs
    }

}