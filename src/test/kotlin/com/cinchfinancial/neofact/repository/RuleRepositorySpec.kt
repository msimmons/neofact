package com.cinchfinancial.neofact.repository

import com.cinchfinancial.neofact.NeoConfig
import com.cinchfinancial.neofact.model.*
import com.cinchfinancial.neofact.service.RuleSetEvaluator
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal

/**
 * Created by mark on 9/1/16.
 */
class RuleRepositorySpec : SpringBehaviorSpec(NeoConfig::class.java) {

    @Autowired
    lateinit var ruleSetRepository: RuleSetRepository
    @Autowired
    lateinit var ruleRepository: RuleRepository
    @Autowired
    lateinit var modelInputRepository: ModelInputRepository
    @Autowired
    lateinit var ruleStatementRepository: RuleStatementRepository

    init {

        Given("A ruleset, inputs and facts") {
            ruleSetRepository.deleteAll()
            ruleRepository.deleteAll()
            modelInputRepository.deleteAll()
            ruleStatementRepository.deleteAll()
            val rs = RuleSet("STR-B1-v9")
            rs.rules.add(RuleSetHasRule(rs, createRule("STRB1G1001", listOf("and(new_purchase_debt_percentage=0.0,debt_amount=0)"), listOf("new_purchase_debt_percentage","debt_amount"), "no_financing_need"),0))
            rs.rules.add(RuleSetHasRule(rs, createRule("STRB1G1002", listOf("and(new_purchase_debt_percentage=0.0, estimated_interest_rate_paid=0.0)"), listOf("new_purchase_debt_percentage", "estimated_interest_rate_paid"), "no_financing_need"),1))
            rs.rules.add(RuleSetHasRule(rs, createRule("STRB1G2003", listOf("and(money_to_use_from_checking>debt_amount, payment_behavior_sloppy<>1, real_loan_value<14000.00)"), listOf("money_to_use_from_checking", "payment_behavior_sloppy","real_loan_value","debt_amount"),"new_0%_credit_card"),2))
            rs.rules.add(RuleSetHasRule(rs, createRule("STRB1G2004", listOf("money_to_use_from_checking>debt_amount"), listOf("money_to_use_from_checking","debt_amount"), "keep_existing_card_and_payoff_with_money_in_checking"),3))
            rs.rules.add(RuleSetHasRule(rs, createRule("STRB1G3005", listOf("payment_behavior_sloppy=1"), listOf("payment_behavior_sloppy"), "keep_existing_card_hard_to_qualify_for_new_product"),4))
            rs.rules.add(RuleSetHasRule(rs, createRule("STRB1G5007", listOf("and(real_loan_value<14000.00, zero_percent_repayment_months_with_checking<5, zero_percent_repayment_months_with_checking >0)"), listOf("real_loan_value","zero_percent_repayment_months_with_checking"), "keep_existing_card_and_payoff_quickly"),5))
            rs.rules.add(RuleSetHasRule(rs, createRule("STRB1G5008", listOf("and(preapproved_for_loan, afford_7_year_personal_loan)"), listOf("preapproved_for_loan", "afford_7_year_personal_loan"), "personal_loan"),6))

            ruleSetRepository.save(rs)

            val values = mutableMapOf<String, Any?>()
            values.put("new_purchase_debt_percentage", BigDecimal.valueOf(0.0))
            //values.put("new_purchase_debt_percentage", null)
            values.put("estimated_interest_rate_paid", BigDecimal.valueOf(12.1))
            values.put("money_to_use_from_checking", BigDecimal.valueOf(875.0))
            values.put("debt_amount", BigDecimal.valueOf(950.0))
            values.put("payment_behavior_sloppy", 1)
            values.put("real_loan_value", BigDecimal.valueOf(900.0))
            values.put("zero_percent_repayment_months_with_checking", 3)
            values.put("preapproved_for_loan", true)
            values.put("afford_7_year_personal_loan", true)
            values.put("credit_card_balance_sum", BigDecimal.valueOf(32.10))
            values.put("checking_balance_sum", BigDecimal.valueOf(100.10))
            values.put("user_profile_employment_length", 3L)
            values.put("a_list_of_values", listOf(1,5,3,6,18))

            When("We evaluate the rule set") {
                val evaluator = RuleSetEvaluator()
                val results = evaluator.evaluate(ruleSetRepository.findAll(2).first(), values)
                results.sortedBy { it.rule.sequence }.forEach {
                    println("${it.rule.rule.name} ${it.passed} ${it.missingInputs}")
                }
                Then("we should get the correct result") {
                    1 shouldBe 1
                }
            }
        }

    }

    fun createRule(name : String, formulas : List<String>, inputs : List<String>, strategy : String) : Rule {
        val r = Rule(name)
        formulas.forEach { formula ->
            val s = RuleStatement(formula)
            inputs.forEach { input ->
                var modelInput = modelInputRepository.findByName(input)
                if ( modelInput == null ) {
                    modelInput = ModelInput(input, "")
                    modelInputRepository.save(modelInput)
                }
                s.inputs.add(modelInput)
            }
            ruleStatementRepository.save(s)
            r.statements.add(s)
        }
        r.strategies.add(RuleSuggestsStrategy(r, Strategy(strategy), "reason"))
        return r
    }
}