package com.example.calculator

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * High-precision calculation engine for clean mathematical evaluation
 * and visual numeral formatting (e.g., "12,345").
 */
class CalculatorEngine {
    private val mathContext = MathContext(16, RoundingMode.HALF_UP)
    private val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
    }
    private val integerFormatter = DecimalFormat("#,##0", symbols)

    fun formatNumber(raw: String): String {
        if (raw.isEmpty()) return "0"
        if (raw == "-") return "-"
        if (raw.contains("Error")) return raw

        val isNegative = raw.startsWith("-")
        val clean = if (isNegative) raw.substring(1) else raw

        val hasTrailingDot = clean.endsWith(".")
        val parts = clean.split(".")

        val integerPart = parts[0]
        val formattedInt = if (integerPart.isEmpty()) "0" else {
            try {
                val bd = BigDecimal(integerPart)
                integerFormatter.format(bd)
            } catch (e: Exception) {
                integerPart
            }
        }

        val result = if (parts.size > 1) {
            val fractionalPart = parts[1]
            "$formattedInt.$fractionalPart"
        } else if (hasTrailingDot) {
            "$formattedInt."
        } else {
            formattedInt
        }

        return if (isNegative) "-$result" else result
    }

    fun calculate(first: String, second: String, operator: String): CalculationResult {
        return try {
            val a = BigDecimal(first.replace(",", ""))
            val b = BigDecimal(second.replace(",", ""))

            val result = when (operator) {
                "+" -> a.add(b, mathContext)
                "-" -> a.subtract(b, mathContext)
                "×" -> a.multiply(b, mathContext)
                "÷" -> {
                    if (b.compareTo(BigDecimal.ZERO) == 0) {
                        return CalculationResult.Error("Cannot divide by 0")
                    }
                    a.divide(b, 10, RoundingMode.HALF_UP).stripTrailingZeros()
                }
                "%" -> {
                    // Percentage calculation
                    a.multiply(b, mathContext).divide(BigDecimal(100), 10, RoundingMode.HALF_UP).stripTrailingZeros()
                }
                else -> b
            }

            // Strip trailing zeros for clean display
            val plain = result.stripTrailingZeros().toPlainString()
            CalculationResult.Success(plain)
        } catch (e: Exception) {
            CalculationResult.Error("Error")
        }
    }

    fun applyPercent(raw: String): String {
        return try {
            val v = BigDecimal(raw.replace(",", ""))
            val result = v.divide(BigDecimal(100), 10, RoundingMode.HALF_UP).stripTrailingZeros()
            result.toPlainString()
        } catch (e: Exception) {
            raw
        }
    }

    fun toggleSign(raw: String): String {
        if (raw == "0" || raw.isEmpty()) return raw
        return if (raw.startsWith("-")) raw.substring(1) else "-$raw"
    }
}

sealed class CalculationResult {
    data class Success(val value: String) : CalculationResult()
    data class Error(val message: String) : CalculationResult()
}
