package com.cinchfinancial.neofact.repository

import com.cinchfinancial.neofact.NeoConfig
import com.cinchfinancial.neofact.model.Outcome
import com.cinchfinancial.neofact.model.Rule
import com.cinchfinancial.neofact.model.RuleAssertsOutcome
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by mark on 9/1/16.
 */
class RuleRepositorySpec : SpringBehaviorSpec(NeoConfig::class.java) {

    @Autowired
    lateinit var ruleRepository: RuleRepository
    @Autowired
    lateinit var outcomeRepository: OutcomeRepository

    init {

        ruleRepository.deleteAll()
        outcomeRepository.deleteAll()

        Given("A ruleset, inputs and facts") {
            val outcome = Outcome("anOutcome")
            outcome["prop1"] = 1
            outcome["prop2"] = "hello"
            outcome["prop3"] = mapOf("key1" to "value1", "key2" to "value2")
            outcomeRepository.save(outcome)
            val rule = Rule("aRule")
            val ruleOutcome = RuleAssertsOutcome(rule, outcome, arrayOf("aReason", "anotherReason"))
            ruleOutcome.reasons.add("anotherReason")
            rule.outcomes.add(ruleOutcome)
            ruleRepository.save(rule, 2)

            val o1 = outcomeRepository.findOne(outcome.id)

            Then("it has the properties") {
                o1["prop1"] shouldEqual outcome["prop1"]
                o1["prop2"] shouldEqual outcome["prop2"]
            }
        }
    }
}
