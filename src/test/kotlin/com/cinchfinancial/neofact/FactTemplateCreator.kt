package com.cinchfinancial.neofact

import io.kotlintest.specs.StringSpec
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

/**
 * Created by mark on 9/1/16.
 */
class FactTemplateCreator : StringSpec() {

    init {
        "Read the fact template file and convert to worksheet" {
            val wb = XSSFWorkbook()
            val factSheet = wb.createSheet("Facts")
            val inputSheet = wb.createSheet("Inputs")
            val ruleSheet = wb.createSheet("Rules")
            File("/Users/mark/work/neofact/src/test/resources/facts.txt").readLines().forEachIndexed { i, fact ->
                val row = factSheet.createRow(i)
                val nameCell = row.createCell(0)
                val valueCell = row.createCell(1)
                nameCell.setCellValue(fact)
                valueCell.setCellValue(0.0)
                val name = wb.createName()
                name.nameName = fact
                name.refersToFormula = "${factSheet.sheetName}!${valueCell.reference}"
            }
            wb.write(File("/Users/mark/work/neofact/src/test/resources/fact_template.xlsx").outputStream())
        }.config(ignored = true)
    }

}