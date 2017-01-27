package com.cinchfinancial.neofact.service

import com.cinchfinancial.neofact.model.ModelInput
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.poi.ss.SpreadsheetVersion
import org.apache.poi.ss.formula.eval.ErrorEval
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Name
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.AreaReference
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import kotlin.system.measureTimeMillis

/**
 * Represent and evaluate models as spreadsheets.  The evaluator is initialized with the known list of
 * fact names and model inputs.  The sheet can then be repeatedly evaluated with different fact values
 */
class ModelEvaluator(val factNames: Set<String>, val modelInputs: Collection<ModelInput>) {

    private val logger: Log = LogFactory.getLog(javaClass)

    private val wb: Workbook = XSSFWorkbook()
    private val factSheet = wb.createSheet("Facts")
    private val inputSheet = wb.createSheet("Inputs")
    private val ruleSheet = wb.createSheet("Rules")
    private val evaluator = wb.creationHelper.createFormulaEvaluator()

    init {
        var row = 0
        factNames.forEach { addNameToSheet(factSheet, row++, it) }
        row = 0
        modelInputs.forEach { addNameToSheet(inputSheet, row++, it.name) }
        modelInputs.forEach { addFormulaToSheet(inputSheet, it.name, it.formula) }
    }

    /**
     * Evaluate the model inputs using the given facts
     * and return the calculated values of the model inputs
     */
    fun evaluateInputs(facts : Map<String,Any?>) : Map<String, Any?> {
        // Add each fact to the fact sheet
        facts.forEach { entry ->
            addToSheet(factSheet, entry.key, entry.value)
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
                        when(cell.cachedFormulaResultType) {
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
        return result
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
    private fun addToSheet(sheet: Sheet, key: String, value: Any?) {
        val namedRange = wb.getName(key) ?: throw IllegalStateException("No named range found for $key")
        val reference = AreaReference(namedRange.refersToFormula, SpreadsheetVersion.EXCEL2007)
        val cell = sheet.getRow(reference.allReferencedCells[0].row).getCell(reference.allReferencedCells[0].col.toInt())
        when ( value ) {
            is Collection<Any?> -> addCollectionToSheet(namedRange, value)
            else -> addScalarToSheet(namedRange, value)
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
     * Add a scalar value to the sheet at the given named range
     */
    private fun addScalarToSheet(namedRange: Name, value: Any?) {
        val reference = AreaReference(namedRange.refersToFormula, SpreadsheetVersion.EXCEL2007)
        val sheet = wb.getSheet(namedRange.sheetName)
        val cell = sheet.getRow(reference.firstCell.row).getCell(reference.firstCell.col.toInt())
        logger.debug("${namedRange.nameName}: ${namedRange.refersToFormula} = $value")
        setCellValue(cell, value)
    }

    /**
     * Add a collection value to the sheet as a named range
     */
    private fun addCollectionToSheet(namedRange: Name, values: Collection<Any?>) {
        val reference = AreaReference(namedRange.refersToFormula, SpreadsheetVersion.EXCEL2007)
        val sheet = wb.getSheet(namedRange.sheetName)
        val row = sheet.getRow(reference.firstCell.row)
        val cells = mutableListOf<Cell>()
        values.forEachIndexed { col, value ->
            val valueCell = row.createCell(col+1)
            cells.add(valueCell)
            setCellValue(valueCell, value)
        }
        namedRange.refersToFormula = "${sheet.sheetName}!${cells.first().address.formatAsString()}:${cells.last().address.formatAsString()}"
        logger.debug("${namedRange.nameName}: ${namedRange.refersToFormula} = $values")
    }

}