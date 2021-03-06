package com.cinchfinancial.neofact.service

import com.cinchfinancial.neofact.model.ModelInput
import com.cinchfinancial.neofact.model.Rule
import com.cinchfinancial.neofact.model.RuleSet
import com.cinchfinancial.neofact.model.RuleSetHasRule
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.poi.ss.SpreadsheetVersion
import org.apache.poi.ss.formula.eval.ErrorEval
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.AreaReference
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import kotlin.system.measureTimeMillis

/**
 * Evaluate a ruleset by collecting all the facts, model inputs and rule statements and evaluating them in a worksheet
 */
class RuleSetEvaluator {

    private val logger: Log = LogFactory.getLog(javaClass)

    private val wb: Workbook = XSSFWorkbook()
    private val factSheet = wb.createSheet("Facts")
    private val inputSheet = wb.createSheet("Inputs")
    private val ruleSheet = wb.createSheet("Rules")
    private val evaluator = wb.creationHelper.createFormulaEvaluator()

    /**
     * Evaluate the given model inputs against the given facts
     * and return the calculated values of the model inputs
     */
    fun evaluate(modelInputs: Collection<ModelInput>, facts: Map<String, Any?>): Map<String, Any?> {
        // Add each fact to the fact sheet
        var row = 0
        facts.forEach { entry ->
            addToSheet(factSheet, ++row, entry.key, entry.value)
        }
        // Add each model input the input sheet
        row = 0
        modelInputs.forEach { input ->
            addNameToSheet(inputSheet, ++row, input.name)
        }
        modelInputs.forEach { input ->
            addFormulaToSheet(inputSheet, input.name, input.formula)
        }

        // Evaluate all inputs
        val elapsed = measureTimeMillis {
            evaluator.evaluateAll()
        }
        logger.debug("Evaulation time: $elapsed ms")

        // Collect values of all inputs
        val result = mutableMapOf<String, Any?>()
        modelInputs.forEach { input ->
            val name = wb.getName(input.name)
            if (name == null) null
            else {
                val reference = AreaReference(name.refersToFormula, SpreadsheetVersion.EXCEL2007)
                val cell = inputSheet.getRow(reference.allReferencedCells[0].row).getCell(reference.allReferencedCells[0].col.toInt())
                when (cell.cellType) {
                    Cell.CELL_TYPE_FORMULA -> {
                        when (cell.cachedFormulaResultType) {
                            Cell.CELL_TYPE_ERROR -> result[input.name] = ErrorEval.getText(cell.errorCellValue.toInt())
                            Cell.CELL_TYPE_BOOLEAN -> result[input.name] = cell.booleanCellValue
                            Cell.CELL_TYPE_NUMERIC -> result[input.name] = BigDecimal.valueOf(cell.numericCellValue)
                            Cell.CELL_TYPE_STRING -> result[input.name] = cell.stringCellValue
                            else -> result[input.name] = null
                        }
                    }
                }
            }
        }
        //wb.write(File("inputs.xlsx").outputStream())
        return result
    }

    /**
     * Evaluate the given ruleset against the given facts
     * and return the RuleSetRules that evaluate as true
     */
    fun evaluate(ruleSet: RuleSet, facts: Map<String, Any?>): List<Result> {
        // Add each fact to the fact sheet
        var row = 0
        facts.forEach { entry ->
            addToSheet(factSheet, ++row, entry.key, entry.value)
        }
        //  Add each input to the fact sheet
        ruleSet.rules.forEach {
            it.rule.inputs.forEach {
                addToSheet(factSheet, ++row, it)
            }
        }
        // Add the rule statements to the rule sheet
        row = 0
        ruleSet.rules.forEach {
            addToSheet(ruleSheet, ++row, it.rule)
        }
        // Evaluate the sheet
        evaluator.evaluateAll()

        // Create the list of results
        val results: List<Result> = ruleSet.rules.map { rule ->
            val missingInputs = rule.rule.inputs.filter { input ->
                val name = wb.getName(input.name)
                if (name == null) true
                else {
                    val reference = AreaReference(name.refersToFormula, SpreadsheetVersion.EXCEL2007)
                    val cell = factSheet.getRow(reference.allReferencedCells[0].row).getCell(reference.allReferencedCells[0].col.toInt())
                    when (cell.cellType) {
                        Cell.CELL_TYPE_ERROR -> true
                        else -> false
                    }
                }
            }.map(ModelInput::name)
            val name = wb.getName(rule.rule.name)
            val reference = AreaReference(name.refersToFormula, SpreadsheetVersion.EXCEL2007)
            val cell = ruleSheet.getRow(reference.allReferencedCells[0].row).getCell(reference.allReferencedCells[0].col.toInt())
            Result(rule, cell.booleanCellValue, missingInputs)
        }
        return results
    }

    /**
     * Add a named range only to the sheet for forward declarations
     * of inputs used by other inputs
     */
    private fun addNameToSheet(sheet: Sheet, row: Int, name: String) {
        val currentRow = sheet.createRow(row)
        val nameCell = currentRow.createCell(0)
        nameCell.setCellValue(name)
        val valueCell = currentRow.createCell(1)
        val namedRange = wb.createName()
        namedRange.nameName = name
        namedRange.refersToFormula = "${sheet.sheetName}!${valueCell.address.formatAsString()}"
        logger.debug("$name: ${namedRange.refersToFormula}")
    }

    private fun addFormulaToSheet(sheet: Sheet, name: String, formula: String) {
        val namedRange = wb.getName(name) ?: return
        val reference = AreaReference(namedRange.refersToFormula, SpreadsheetVersion.EXCEL2007)
        val cell = sheet.getRow(reference.allReferencedCells[0].row).getCell(reference.allReferencedCells[0].col.toInt())
        logger.debug("Adding formula for $name: $formula")
        cell.cellFormula = formula
    }

    /**
     * Add a fact value to the sheet
     */
    private fun addToSheet(sheet: Sheet, row: Int, key: String, value: Any?) {
        val currentRow = sheet.createRow(row)
        val nameCell = currentRow.createCell(0)
        nameCell.setCellValue(key)
        when (value) {
            is Collection<Any?> -> addCollectionToSheet(sheet, currentRow, key, value)
            else -> addScalarToSheet(sheet, currentRow, key, value)
        }
    }

    /**
     * Set a single cell value according to its type
     */
    private fun setCellValue(cell: Cell, value: Any?) {
        when (value) {
            null -> cell.setCellErrorValue(ErrorEval.VALUE_INVALID.errorCode.toByte())
            is Boolean -> cell.setCellValue(value)
            is Number -> cell.setCellValue(value.toDouble())
            is BigDecimal -> cell.setCellValue(value.toDouble())
            is String -> cell.setCellValue(value)
            is LocalDateTime -> cell.setCellValue(value as Date)
            else -> cell.setCellValue(value.toString())
        }
    }

    /**
     * Add a scalar value to the sheet as a named range of one cell
     */
    private fun addScalarToSheet(sheet: Sheet, row: Row, key: String, value: Any?) {
        val valueCell = row.createCell(1)
        val name = wb.createName()
        name.nameName = key
        name.refersToFormula = "${sheet.sheetName}!${valueCell.address.formatAsString()}"
        logger.debug("$key: ${name.refersToFormula}")
        setCellValue(valueCell, value)
    }

    /**
     * Add a collection value to the sheet as a named range
     */
    private fun addCollectionToSheet(sheet: Sheet, row: Row, key: String, values: Collection<Any?>) {
        val cells = mutableListOf<Cell>()
        values.forEachIndexed { col, value ->
            val valueCell = row.createCell(col + 1)
            cells.add(valueCell)
            setCellValue(valueCell, value)
        }
        val name = wb.createName()
        name.nameName = key
        name.refersToFormula = "${sheet.sheetName}!${cells.first().address.formatAsString()}:${cells.last().address.formatAsString()}"
        logger.debug("$key: ${name.refersToFormula}")
    }

    /**
     * Add a calculated input to the sheet
     */
    private fun addToSheet(sheet: Sheet, row: Int, input: ModelInput) {
        if (input.formula.isNullOrEmpty()) return
        if (wb.getName(input.name) != null) return
        val currentRow = sheet.createRow(row)
        val nameCell = currentRow.createCell(0)
        nameCell.setCellValue(input.name)
        val valueCell = currentRow.createCell(1)
        val name = wb.createName()
        name.nameName = input.name
        name.refersToFormula = "${sheet.sheetName}!${valueCell.address.formatAsString()}"
        logger.debug("Adding formula for ${input.name}: ${input.formula}")
        valueCell.cellFormula = input.formula
    }

    /**
     * Add a rule and its statements to the sheet
     */
    private fun addToSheet(sheet: Sheet, row: Int, rule: Rule) {
        val currentRow = sheet.createRow(row)
        var column = 0
        val nameCell = currentRow.createCell(column)
        nameCell.setCellValue(rule.name)
        val valueCell = currentRow.createCell(++column)
        val name = wb.createName()
        name.nameName = rule.name
        name.refersToFormula = "${sheet.sheetName}!${valueCell.address.formatAsString()}"
        // Add each eval formula as a column on this row
        val formulaCells = mutableListOf<Cell>()
        column++
        val formulaCell = currentRow.createCell(column)
        try {
            formulaCell.cellFormula = rule.formula
        } catch (e: Exception) {
            formulaCell.setCellErrorValue(ErrorEval.REF_INVALID.errorCode.toByte())
        }
        formulaCells += formulaCell
        valueCell.cellFormula = formulaCells.joinToString(",", "and(", ")")
        {
            it.address.formatAsString()
        }
    }
    /**
     * The result of a rule set evaluation is a ruleSetHasRule, whether it passed or not and any missing inputs
     */
    class Result(val rule: RuleSetHasRule, val passed: Boolean, val missingInputs: List<String>)
}

