
import com.cinchfinancial.neofact.dsl.model
import java.math.BigDecimal

operator fun BigDecimal.plus(i : Int) : BigDecimal { return this.add(BigDecimal.valueOf(i.toLong())) }
operator fun BigDecimal.div(i : Int) : BigDecimal { return this.divide(BigDecimal.valueOf(i.toLong())) }
operator fun Int.invoke() : BigDecimal = BigDecimal.valueOf(this.toLong())
fun Int.between(min: Int, max: Int) : Boolean = this >= min && this <  max
infix fun Double.B(scale:Int) : BigDecimal {return BigDecimal.valueOf(this).setScale(scale)}

model {

  // Lookup Tables
  val InsuranceCost = table {
    //row("Insurance Cost @ $0.25MM", "Smoker", "NonSmoker")
    row(25,55.71,27.53)
    row(30,60.17,27.88)
    row(35,67.42,29.98)
    row(40,97.95,36.03)
    row(45,156.80,51.62)
    row(50,233.21,76.58)
    row(55, 372.47, 121.28)
    row(60, 555.73, 207.70)
    row(65, 988.32, 347.67)
  }

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
    row(0.0, "r")
    row(0.5, "o")
    row(1.0, "g")
  }

  // Facts
  val user_age : Int by facts
  val user_number_of_children : Int by facts
  val user_is_homeowner : Boolean by facts
  val user_is_smoker : Boolean by facts
  val user_checking_monthly_net_income : Double by facts
  val user_checking_ending_balance : Double by facts
  val user_savings_ending_balance : Double by facts
  val user_unlinked_cash : Double by facts
  val user_life_insurance_coverage_amount : Double by facts

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
  val LifeCoverageMultiple by formula { /*SUMIFS(index(Table_AgeGroupProtection,0,4),index(Table_AgeGroupProtection,0,1),5+CEILING(Age-5,10),index(Table_AgeGroupProtection,0,2),NumDependents>0,index(Table_AgeGroupProtection,0,3),IsHomeOwner)*/ 0.5 }
  val InsuranceCost25 by formula { /*VLOOKUP(CEILING(Age,5),Table_InsuranceCost,IF(IsSmoker,2,3),0)*/
    val f = InsuranceCost.data.first { (it[0] as Int).between(Age, Age+5) }?.get(if (IsSmoker) 1 else 2) as Double?
  }
  val LifeCoverageGap by formula { /*max(0,(MonthlyNetIncome*LifeCoverageMultiple-TotalCash-ExistingLifeCoverage).multiply(12))*/
    maxOf(0.0, (MonthlyNetIncome*LifeCoverageMultiple-TotalCash-ExistingLifeCoverage)*12.0)
  }
  val LifeCoverageCost by formula { InsuranceCost25 ?: 0*LifeCoverageGap/250000 }
  val LifeCoverageGrade by formula { /*VLOOKUP(ExistingLifeCoverage/(ExistingLifeCoverage+LifeCoverageGap),Tables_Life_Coverage_Buckets,2,1)*/
    LifeCoverageBuckets.data.first { (it[0] as Double) >= ExistingLifeCoverage/(ExistingLifeCoverage+LifeCoverageGap)}?.get(1) as String?
  }
}