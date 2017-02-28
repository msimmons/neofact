package com.cinchfinancial.neofact.service

import com.cinchfinancial.neofact.NeoConfig
import com.cinchfinancial.neofact.model.*
import com.cinchfinancial.neofact.repository.*
import org.apache.poi.ss.formula.WorkbookEvaluator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import java.io.InputStreamReader
import java.math.BigDecimal

/**
 * Created by mark on 1/12/17.
 */
class ModelEvaluatorSpec : SpringBehaviorSpec(NeoConfig::class.java) {
    @Autowired
    lateinit var ruleSetRepository: RuleSetRepository

    @Autowired
    lateinit var ruleRepository: RuleRepository

    @Autowired
    lateinit var modelInputRepository: ModelInputRepository

    @Autowired
    lateinit var factRepository: FactRepository

    @Autowired
    lateinit var outcomeRepository: OutcomeRepository

    val keywords = setOf("and","if","sum","max","min","or")

    init {
        // clean the db
        ruleSetRepository.deleteAll()
        ruleRepository.deleteAll()
        modelInputRepository.deleteAll()
        factRepository.deleteAll()
        outcomeRepository.deleteAll()
        // Create some model inputs
        val inputs = createModelInputs()
        val facts = mutableMapOf<String,Any?>()
        facts["user_profile.transunion.fico_score_amount"] = 750
        facts["user_profile.mx.debt_income_ratio"] = BigDecimal.valueOf(.66)
        facts["user_profile.user.employed_less_than_a_year_condition"] = true
        facts["user_profile.transunion.delinquencies_last_2_years_count"] = 0
        facts["user_profile.transunion.derogatory_public_records"] = 0
        facts["user_profile.user.income_gross_annual_amount"] = BigDecimal.valueOf(140000)
        facts["user_profile.transunion.credit_inquiries_last_6_months_count"] = 0
        facts["user_profile.mx.debt_income_ratio"] = BigDecimal.valueOf(.5)
        facts["user_profile.transunion.debt_accounts_count"] = 2
        facts["credit_card.mx.utilization_amount"] = 50
        facts["credit_card.mx.balance_sum"] = 2000
        facts["user_profile.transunion.credit_history_months"] = 3
        facts["user_profile.transunion.bankruptcies_public_count"] = 0
        facts["user_profile.transunion.tax_liens_count"] = 0
        facts["user_profile.mx.debt_repayment_0_percent_months"] = 1
        facts["user_profile.mx.personal_loan_7_year_afford_condition"] = true
        facts["credit_card.mx.interest_rate_estimated_current_average"] = 13
        facts["credit_card.mx.interest_rate_signal_average"] = 13
        facts["user_profile.mx.behavior_revolver_0_percent_condition"] = false
        facts["user_profile.user.debt_payment_cash_available_amount"] = 2000
        facts["checking.mx.flow_net_average"] = 300
        facts["credit_card.mx.payment_behavior_sloppy_condition"] = false
        facts["user_profile.system.credit_card_preapproval_condition"] = true
        facts["user_profile.system.personal_loan_preapproval_condition"] = true
        facts["credit_card.mx.credits_payments_average"] = 300
        facts["credit_card.mx.credits_payments_30_day_average_sum"] = 300
        facts["credit_card.mx.interest_rate_signal_average"] = 13
        facts["credit_card.accounts.transunion.balance_ending_amount"] = listOf(BigDecimal.valueOf(100.0), BigDecimal.valueOf(400.21), BigDecimal.valueOf(32.40))
        facts["checking.mx.credits_income_average"] = 40
        facts["credit_card.mx.debits_purchases_average"] = 3000
        facts["checking.mx.balance_sum"] = BigDecimal.valueOf(2000)
        facts["user_profile.system.personal_loan_3_year_apr_mid_amount"] = 8
        facts["user_profile.system.personal_loan_3_year_apr_high_amount"] = 10
        facts["user_profile.system.personal_loan_3_year_apr_low_amount"] = 6
        facts["user_profile.system.personal_loan_5_year_apr_mid_amount"] = 8
        facts["user_profile.system.personal_loan_5_year_apr_high_amount"] = 10
        facts["user_profile.system.personal_loan_5_year_apr_low_amount"] = 6
        facts["user_profile.system.credit_card_apr_prediction"] = 14
        facts["user_profile.transunion.address_state_name"] = "MA"
        facts["user_profile.mx.debits_payments_mobile_phone_current_carrier_name"] = "VERIZON"
        facts["user_profile.mx.debits_payments_mobile_phone_30_day_average"] = BigDecimal.valueOf(50.00)
        facts["user_profile.mx.deposits_sum"] = BigDecimal.valueOf(3000)
        facts["user_profile.mx.banking_debits_30_day_average"] = 300
        facts["user_profile.mx.banking_credits_30_day_average"] = 500
        facts["user_profile.mx.debt_service_amount"] = 300
        facts["credit_card.mx.credits_30_day_average_sum"] = 40
        facts["credit_card.mx.debits_30_day_average_sum"] = 50
        facts["credit_card.mx.payment_minimum_average_sum"] = 20
        facts["credit_card.mx.balance_ending_balance_beginning_delta"] = 50
        facts["checking.mx.balance_beginning_sum"] = 1000
        val evaluator = ModelEvaluator(facts.keys, inputs.values, emptyMap())
        val evaluator2 = ModelEvaluator(facts.keys, inputs.values, emptyMap())
        var inputValues : Map<String,Any?> = mapOf()
        inputValues = evaluator.evaluateInputs(facts)
        evaluator2.evaluateInputs(facts)
        inputValues.forEach { s, any -> println("$s: $any") }
        println("Average Evaluation time: ${evaluator.evaluationTime/evaluator.evaluationCount}")
        println("Average Collection time: ${evaluator.collectionTime/evaluator.evaluationCount}")

        // Do it again with some different fact values, especially the collection
        facts["credit_card.accounts.transunion.balance_ending_amount"] = listOf(BigDecimal.valueOf(100.0), BigDecimal.valueOf(400.21), BigDecimal.valueOf(32.40), BigDecimal.valueOf(33.50))
        facts["credit_card.mx.credits_30_day_average_sum"] = 1000

        inputValues = evaluator.evaluateInputs(facts)

        // Do it again with some different fact values, especially the collection
        facts["credit_card.accounts.transunion.balance_ending_amount"] = listOf(BigDecimal.valueOf(10.0), BigDecimal.valueOf(40.21), BigDecimal.valueOf(332.40), BigDecimal.valueOf(330.50))
        facts["credit_card.mx.credits_30_day_average_sum"] = 4

        inputValues = evaluator.evaluateInputs(facts)
        inputValues = evaluator.evaluateInputs(facts)

        inputValues.forEach { s, any -> println("$s: $any") }

        println("Setup time: ${evaluator.setupTime}")
        println("Average Evaluation time: ${evaluator.evaluationTime/evaluator.evaluationCount}")
        println("Average Collection time: ${evaluator.collectionTime/evaluator.evaluationCount}")
        println("Memory Used: ${Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()}")
    }

    private fun createModelInputs() : Map<String, ModelInput> {
        var name : String = ""
        var type : ModelInputType = ModelInputType.string
        var estimated : Boolean = false
        var formula : String = ""
        val modelInputs = mutableMapOf<String, ModelInput>()
        InputStreamReader(ClassPathResource("inputs.txt").inputStream).forEachLine {
            if ( it.contains("Name:") && name.isNotEmpty() ) {
                modelInputs[name] = createModelInput(name, type, estimated, formula)
                name = ""
                formula = ""
            }
            if ( it.contains("Name:") ) name = it.split(":")[1].trim()
            if ( it.contains("Type:") ) type = ModelInputType.valueOf(it.split(":")[1].trim())
            if ( it.contains("Estimated:") ) estimated = it.split(":")[1].trim().toBoolean()
            if ( it.contains("Formula:") )  formula = it.split(":")[1].trim()
            if ( it.startsWith(" ") && formula.isNotEmpty() ) formula += " " + it.trim()
        }
        return modelInputs
    }

    private fun createModelInput(name : String, type: ModelInputType, estimated: Boolean, formula: String) : ModelInput {
        val modelInput = modelInputRepository.findByName(name) ?: ModelInput(name, formula)
        modelInput.formula = formula
        modelInput.estimated = estimated
        modelInput.type = type
        modelInputRepository.save(modelInput, 3)
        formula.split(" ","/","<",">","+","-","*","(",")",",","=").forEach {
            if ( it.matches(Regex("([a-zA-Z]+).*")) && !WorkbookEvaluator.getSupportedFunctionNames().contains(it) ) modelInput.facts.add(createFact(it))
        }
        modelInputRepository.save(modelInput, 3)
        return modelInput
    }

    private fun createFact(name: String) : Fact {
        when ( name.contains("ci.") ) {
            true -> return modelInputRepository.findByName(name) ?: modelInputRepository.save(ModelInput(name, ""), 3)
            false -> return factRepository.findByName(name) ?: factRepository.save(Fact(name), 3)
        }
    }

    private fun createRuleSet(inputs: Map<String,ModelInput>) : RuleSet {
        val strategy = Outcome("strategy1")
        outcomeRepository.save(strategy, 2)
        val rule = Rule("rule1")
        rule.outcomes + RuleAssertsOutcome(rule, strategy, arrayOf("because"))

        val ruleSet = RuleSet("test")
        ruleSetRepository.save(ruleSet, 2)
        ruleSet.rules += RuleSetHasRule(ruleSet, Rule("rule1"), 0)
        return ruleSet
    }
}