
import com.cinchfinancial.neofact.dsl.model
import java.math.BigDecimal

operator fun BigDecimal.plus(i : Int) : BigDecimal { return this.add(BigDecimal.valueOf(i.toLong())) }
operator fun BigDecimal.div(i : Int) : BigDecimal { return this.divide(BigDecimal.valueOf(i.toLong())) }
operator fun Int.invoke() : BigDecimal = BigDecimal.valueOf(this.toLong())

model {
  /**
  facts["user.age"] = 25
  facts["user.number_of_children"] = 1
  facts["user.is_homeowner"] = true
  facts["user.is_smoker"] = false
  facts["user.checking.monthly_net_income"] = BigDecimal.valueOf(6133)
  facts["user.checking.ending_balance"] = BigDecimal.valueOf(2000)
  facts["user.savings.ending_balance"] = BigDecimal.valueOf(300)
  facts["user.unlinked_cash"] = BigDecimal.valueOf(500)
  facts["user.life_insurance.coverage_amount"] = 100000
   */

  // Lookup Tables
  val AgeGroupProtection = table {
    row(25, true, true, 4)
    row(25, true, false, 4)
    row(25, false, true, 1)
    row(25, false, false, 0)
    row(35, true, true, 5)
    row(35, true, false, 5)
    row(35, false, true, 1)
    row(35, false, false, 0)
    row(45, true, true, 6)
    row(45, true, false, 6)
    row(45, false, true, 1)
    row(45, false, false, 0)
    row(55, true, true, 5.5)
    row(55, true, false, 5.5)
    row(55, false, true, 2)
    row(55, false, false, 1)
    row(65, true, true, 4)
    row(65, true, false, 4)
    row(65, false, true, 2)
    row(65, false, false, 1)
  }

  val LifeCoverageBuckets = table {
    row(0, "r")
    row(0.5, "o")
    row(1, "g")
  }

  // Facts
  val user_age : Integer? by facts
  val user_number_of_children : Integer? by facts
  val user_is_homeowner : Boolean? by facts
  val user_is_smoker : Boolean? by facts
  val user_checking_monthly_net_income : BigDecimal by facts
  val user_checking_ending_balance : BigDecimal by facts
  val user_savings_ending_balance : BigDecimal by facts
  val user_unlinked_cash : BigDecimal by facts
  val user_life_insurance_coverage_amount : BigDecimal by facts

  // Inputs
  val Age by formula { user_age }
  val NumDependents by formula { user_number_of_children }
  val ExistingLifeCoverage by formula { user_life_insurance_coverage_amount }
  val IsHomeOwner by formula { user_is_homeowner }
  val IsSmoker by formula { user_is_smoker }
  val MonthlyNetIncome by formula { user_checking_monthly_net_income }
  val CheckingBalance by formula { user_checking_ending_balance }
  val SavingsBalance by formula { user_savings_ending_balance }
  val ReservesBalance by formula { user_unlinked_cash }
  val TotalCash by formula { CheckingBalance + SavingsBalance + ReservesBalance }
  val LifeCoverageMultiple by formula { /*SUMIFS(index(Table_AgeGroupProtection,0,4),index(Table_AgeGroupProtection,0,1),5+CEILING(Age-5,10),index(Table_AgeGroupProtection,0,2),NumDependents>0,index(Table_AgeGroupProtection,0,3),IsHomeOwner)*/ BigDecimal.valueOf(0.5) }
  val InsuranceCost25 by formula { /*VLOOKUP(CEILING(Age,5),Table_InsuranceCost,IF(IsSmoker,2,3),0)*/ 100()}
  val LifeCoverageGap by formula { /*max(0,(MonthlyNetIncome*LifeCoverageMultiple-TotalCash-ExistingLifeCoverage).multiply(12))*/ 100()}
  val LifeCoverageCost by formula { InsuranceCost25*LifeCoverageGap/250000 }
  val LifeCoverageGrade by formula { LifeCoverageBuckets.data.find { it[0]== ExistingLifeCoverage/(ExistingLifeCoverage+LifeCoverageGap)}?.get(1) as String }
}