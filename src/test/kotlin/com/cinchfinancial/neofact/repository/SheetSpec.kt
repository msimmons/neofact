package com.cinchfinancial.neofact.repository

import io.kotlintest.specs.BehaviorSpec
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.Workbook
import kotlin.system.measureTimeMillis

/**
 * Created by mark on 8/22/16.
 */
class SheetSpec : BehaviorSpec() {
    init {

        Given("A ruleset worksheet") {
            Runtime.getRuntime().gc()
            println("Pre Book Used: ${Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()}")

            val books = mutableListOf<Workbook>()
/*
            (0..10).forEach {
                books.add(XSSFWorkbook(File("/Users/mark/Downloads/life_insurance.xlsx").inputStream()))
            }
*/
/*
            (0..10).forEach {
                books.add(XSSFWorkbook(File("/Users/mark/Downloads/allocation_and_moves.xlsx").inputStream()))
            }
*/
            val evaluators = mutableListOf<FormulaEvaluator>()
            books.forEach {
                evaluators.add(it.creationHelper.createFormulaEvaluator())
            }
            val facts = mapOf<String, Any>("fact1" to 0.00, "fact2" to 0.00, "fact3" to 150.00, "state" to "MA")
            val inputs = setOf<String>("input1", "input2", "rate", "input3", "input4", "get_a_new_card")

            When("We load the given facts and evaulate all the formulas") {
/*
                facts.forEach {
                    val fact = wb.getName(it.key)
                    val aref = AreaReference(fact.getRefersToFormula())
                    val refs = aref.getAllReferencedCells()
                    val sheet = wb.getSheet(refs[0].getSheetName())
                    val row = sheet.getRow(refs[0].getRow())
                    val cell = row.getCell(refs[0].col.toInt())
                    when (cell.cellType) {
                        Cell.CELL_TYPE_STRING -> cell.setCellValue(it.value as String)
                        Cell.CELL_TYPE_NUMERIC -> cell.setCellValue(it.value as Double)
                    }
                }
*/
                var elapsed = measureTimeMillis {
                    evaluators.forEach { it.evaluateAll() }
                }
                //println("Load time: $load")
                println("Evaluation time: $elapsed")
                books.forEach { mod1(it) }
                elapsed = measureTimeMillis {
                    evaluators.forEach { it.evaluateAll() }
                }
                println(Runtime.getRuntime().freeMemory())
                println("Evaluation time: $elapsed")
                books.forEach { mod2(it) }
                elapsed = measureTimeMillis {
                    evaluators.forEach { it.evaluateAll() }
                }
                println("Evaluation time: $elapsed")

                println("Used: ${Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()}")
                Runtime.getRuntime().gc()
                println("Used (GC): ${Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()}")
/*
                inputs.forEach {
                    Then("We get the correct inputs") {
                        val input = wb.getName(it)
                        val aref = AreaReference(input.getRefersToFormula())
                        val refs = aref.getAllReferencedCells()
                        val sheet = wb.getSheet(refs[0].getSheetName())
                        val row = sheet.getRow(refs[0].getRow())
                        val cell = row.getCell(refs[0].col.toInt())
                        val cellType = if (cell.cellType == Cell.CELL_TYPE_FORMULA) cell.getCachedFormulaResultType() else cell.cellType
                        when (cellType) {
                            Cell.CELL_TYPE_STRING -> println("${input.nameName} = ${cell.getStringCellValue()}")
                            Cell.CELL_TYPE_FORMULA -> println("${input.nameName} = ${cell.getNumericCellValue()}")
                            Cell.CELL_TYPE_NUMERIC -> println("${input.nameName} = ${cell.getNumericCellValue()}")
                            Cell.CELL_TYPE_BOOLEAN -> println("${input.nameName} = ${cell.getBooleanCellValue()}")
                            Cell.CELL_TYPE_ERROR -> println("${input.nameName} = ${cell.getErrorCellValue()}")
                        }
                    }
                }
*/
            }
        }
    }

    fun mod1(wb: Workbook) {
        val sheet = wb.getSheet("Inputs")
        var row = sheet.getRow(1)
        val bcell = row.getCell(1)
        bcell.setCellValue(false)
        row = sheet.getRow(2)
        val dcell = row.getCell(1)
        dcell.setCellValue(5000.00)
    }

    fun mod2(wb: Workbook) {
        val sheet = wb.getSheet("Inputs")
        var row = sheet.getRow(1)
        val bcell = row.getCell(1)
        bcell.setCellValue(true)
        row = sheet.getRow(2)
        val dcell = row.getCell(1)
        dcell.setCellValue(10000.00)
    }
}