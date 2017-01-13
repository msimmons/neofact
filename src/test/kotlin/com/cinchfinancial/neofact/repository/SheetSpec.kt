package com.cinchfinancial.neofact.repository

import io.kotlintest.specs.BehaviorSpec
import org.apache.poi.hssf.util.AreaReference
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

/**
 * Created by mark on 8/22/16.
 */
class SheetSpec : BehaviorSpec() {
    init {

        Given("A ruleset worksheet") {
            val wb = XSSFWorkbook(File("/Users/mark/Downloads/fact_check.xlsx").inputStream())
            val evaluator = wb.getCreationHelper().createFormulaEvaluator()
            val facts = mapOf<String, Any>("fact1" to 0.00, "fact2" to 0.00, "fact3" to 150.00, "state" to "MA")
            val inputs = setOf<String>("input1", "input2", "rate", "input3", "input4", "get_a_new_card")

            When("We load the given facts and evaulate all the formulas") {
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
                evaluator.evaluateAll()

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
            }
        }
    }
}