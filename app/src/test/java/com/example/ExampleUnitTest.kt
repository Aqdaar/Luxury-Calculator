package com.example

import com.example.calculator.CalculationResult
import com.example.calculator.CalculatorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    private val engine = CalculatorEngine()

    @Test
    fun testThousandFormatting() {
        assertEquals("12,345", engine.formatNumber("12345"))
        assertEquals("1,000,000", engine.formatNumber("1000000"))
        assertEquals("12,345.67", engine.formatNumber("12345.67"))
        assertEquals("0.", engine.formatNumber("0."))
        assertEquals("-12,345", engine.formatNumber("-12345"))
    }

    @Test
    fun testCalculations() {
        val add = engine.calculate("12", "33", "+")
        assertTrue(add is CalculationResult.Success)
        assertEquals("45", (add as CalculationResult.Success).value)

        val div = engine.calculate("10", "2", "÷")
        assertTrue(div is CalculationResult.Success)
        assertEquals("5", (div as CalculationResult.Success).value)

        val divZero = engine.calculate("10", "0", "÷")
        assertTrue(divZero is CalculationResult.Error)
    }
}
