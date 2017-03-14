// Lookup Tables
var InsuranceCost = [
    [25,55.71,27.53],
    [30,60.17,27.88],
    [35,67.42,29.98],
    [40,97.95,36.03],
    [45,156.80,51.62],
    [50,233.21,76.58],
    [55, 372.47, 121.28],
    [60, 555.73, 207.70],
    [65, 988.32, 347.67]
    ];
var AgeGroupProtection = [
    [25, true, true, 4],
    [25, true, false, 4],
    [25, false, true, 1],
    [25, false, false, 0],
    [35, true, true, 5],
    [35, true, false, 5],
    [35, false, true, 1],
    [35, false, false, 0],
    [45, true, true, 6],
    [45, true, false, 6],
    [45, false, true, 1],
    [45, false, false, 0],
    [55, true, true, 5.5],
    [55, true, false, 5.5],
    [55, false, true, 2],
    [55, false, false, 1],
    [65, true, true, 4],
    [65, true, false, 4],
    [65, false, true, 2],
    [65, false, false, 1]
    ];
var LifeCoverageBuckets = [
    [0.0, "r"],
    [0.5, "o"],
    [1.0, "g"]
    ];
// Facts
var facts = {};

// Inputs
function Age() { return facts.user_age }

function NumDependents() { return facts.user_number_of_children }

function ExistingLifeCoverage() { return facts.user_life_insurance_coverage_amount }

function IsHomeOwner() { return facts.user_is_homeowner }

function IsSmoker() { return facts.user_is_smoker }

function MonthlyNetIncome() { return facts.user_checking_monthly_net_income }

function CheckingBalance()  { return facts.user_checking_ending_balance }

function SavingsBalance() { return facts.user_savings_ending_balance }

function ReservesBalance() { return facts.user_unlinked_cash }

function TotalCash() { return CheckingBalance() + SavingsBalance() + ReservesBalance() }

function LifeCoverageMultiple() { /*SUMIFS(index(Table_AgeGroupProtection,0,4),index(Table_AgeGroupProtection,0,1),5+CEILING(Age-5,10),index(Table_AgeGroupProtection,0,2),NumDependents>0,index(Table_AgeGroupProtection,0,3),IsHomeOwner)*/ return 0.5 }

function InsuranceCost25() { /*VLOOKUP(CEILING(Age,5),Table_InsuranceCost,IF(IsSmoker,2,3),0)*/
    return InsuranceCost.filter(function(it) { return it[0] >= Age() && it[0] < Age()+5 })[0][IsSmoker() ? 1 : 2]
}

function LifeCoverageGap() { /*max(0,(MonthlyNetIncome*LifeCoverageMultiple-TotalCash-ExistingLifeCoverage).multiply(12))*/
    return Math.max(0.0, (MonthlyNetIncome()*LifeCoverageMultiple()-TotalCash()-ExistingLifeCoverage())*12.0)
}

function LifeCoverageCost() { return InsuranceCost25()*LifeCoverageGap()/250000 }

function LifeCoverageGrade() { /*VLOOKUP(ExistingLifeCoverage/(ExistingLifeCoverage+LifeCoverageGap),Tables_Life_Coverage_Buckets,2,1)*/
    return LifeCoverageBuckets.filter( function(it) { return it[0] >= ExistingLifeCoverage()/(ExistingLifeCoverage()+LifeCoverageGap())})[0][1]
}

function inputs() {
  return  {
    Age: Age(),
    NumDependents: NumDependents(),
    ExistingLifeCoverage: ExistingLifeCoverage(),
    IsHomeOwner: IsHomeOwner(),
    IsSmoker: IsSmoker(),
    MonthlyNetIncome: MonthlyNetIncome(),
    CheckingBalance: CheckingBalance(),
    SavingsBalance: SavingsBalance(),
    ReservesBalance: ReservesBalance(),
    TotalCash: TotalCash(),
    LifeCoverageMultiple: LifeCoverageMultiple(),
    InsuranceCost25: InsuranceCost25(),
    LifeCoverageGap: LifeCoverageGap(),
    LifeCoverageCost: LifeCoverageCost(),
    Intermediate: ExistingLifeCoverage()/(ExistingLifeCoverage()+LifeCoverageGap()),
    LifeCoverageGrade: LifeCoverageGrade()
  }
}

function rules() {return {}}