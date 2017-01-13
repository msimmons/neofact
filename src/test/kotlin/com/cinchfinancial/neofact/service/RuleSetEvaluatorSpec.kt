package com.cinchfinancial.neofact.service

import com.cinchfinancial.neofact.NeoConfig
import com.cinchfinancial.neofact.model.*
import com.cinchfinancial.neofact.repository.*
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by mark on 1/12/17.
 */
class RuleSetEvaluatorSpec : SpringBehaviorSpec(NeoConfig::class.java) {
    @Autowired
    lateinit var ruleSetRepository: RuleSetRepository

    @Autowired
    lateinit var ruleRepository: RuleRepository

    @Autowired
    lateinit var modelInputRepository: ModelInputRepository

    @Autowired
    lateinit var ruleStatementRepository: RuleStatementRepository

    @Autowired
    lateinit var strategyRepository: StrategyRepository

    init {
        // Create some model inputs
        val inputs = createModelInputs()
        // Create a ruleset that references the model inputs

        // Evaluate the ruleset against some facts
    }

    private fun createModelInputs() : Map<String, ModelInput> {
        return mapOf()
    }

    private fun createRuleSet(inputs: Map<String,ModelInput>) : RuleSet {
        val strategy = Strategy("strategy1")
        strategyRepository.save(strategy)
        val rule = Rule("rule1")
        rule.strategies += RuleSuggestsStrategy(rule, strategy, "because")
        val statement = RuleStatement("")
        statement.inputs.add(inputs[""] ?: throw IllegalStateException("No such input"))
        rule.statements += RuleStatement("")

        val ruleSet = RuleSet("test")
        ruleSetRepository.save(ruleSet)
        ruleSet.rules += RuleSetHasRule(ruleSet, Rule("rule1"), 0)
        return ruleSet
    }
}