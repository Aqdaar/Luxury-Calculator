package com.example.data.backup

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.data.db.CalculationEntity
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Handles exporting, importing, copying to clipboard, and sharing calculations
 * to Google Drive, WhatsApp, Email, or device storage.
 */
object BackupManager {

    fun copySnippetToClipboard(context: Context, calculation: CalculationEntity) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val dateStr = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(calculation.timestamp))
        val text = buildString {
            appendLine("✨ Luxury Calculator — ${calculation.sessionName}")
            if (calculation.templateTag.isNotEmpty() && calculation.templateTag != "General") {
                appendLine("Tag: [${calculation.templateTag}]")
            }
            appendLine("Formula: ${calculation.formula}")
            appendLine("Total: ${calculation.result}")
            if (calculation.notes.isNotEmpty()) {
                appendLine("Notes: ${calculation.notes}")
            }
            appendLine("Date: $dateStr")
        }

        val clip = ClipData.newPlainText("Calculation Snippet", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Snippet copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun shareCalculation(context: Context, calculation: CalculationEntity) {
        val dateStr = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(calculation.timestamp))
        val text = buildString {
            appendLine("✨ Luxury Calculator — ${calculation.sessionName}")
            if (calculation.templateTag.isNotEmpty() && calculation.templateTag != "General") {
                appendLine("Category: ${calculation.templateTag}")
            }
            appendLine("Expression: ${calculation.formula}")
            appendLine("Result: ${calculation.result}")
            if (calculation.notes.isNotEmpty()) {
                appendLine("Notes: ${calculation.notes}")
            }
            appendLine("Timestamp: $dateStr")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "Luxury Calculator: ${calculation.sessionName}")
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Calculation via...")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun exportToJson(calculations: List<CalculationEntity>): String {
        val root = JSONObject()
        root.put("app", "Luxury Calculator")
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())

        val array = JSONArray()
        for (calc in calculations) {
            val obj = JSONObject().apply {
                put("id", calc.id)
                put("sessionName", calc.sessionName)
                put("templateTag", calc.templateTag)
                put("formula", calc.formula)
                put("result", calc.result)
                put("timestamp", calc.timestamp)
                put("notes", calc.notes)
                put("operand1Tag", calc.operand1Tag)
                put("operand2Tag", calc.operand2Tag)
                put("entryTagsJson", calc.entryTagsJson)
                put("cardColorHex", calc.cardColorHex)
                put("isPinned", calc.isPinned)
            }
            array.put(obj)
        }
        root.put("calculations", array)
        return root.toString(2)
    }

    fun parseFromJson(jsonString: String): List<CalculationEntity> {
        val list = mutableListOf<CalculationEntity>()
        try {
            val root = JSONObject(jsonString)
            val array = root.optJSONArray("calculations") ?: return list
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CalculationEntity(
                        sessionName = obj.optString("sessionName", "Saved Calculation"),
                        templateTag = obj.optString("templateTag", "General"),
                        formula = obj.optString("formula", ""),
                        result = obj.optString("result", "0"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        notes = obj.optString("notes", ""),
                        operand1Tag = obj.optString("operand1Tag", ""),
                        operand2Tag = obj.optString("operand2Tag", ""),
                        entryTagsJson = obj.optString("entryTagsJson", ""),
                        cardColorHex = obj.optLong("cardColorHex", 0x22FFFFFF),
                        isPinned = obj.optBoolean("isPinned", false)
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun shareBackupToDrive(context: Context, jsonBackup: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "Luxury Calculator Backup - ${System.currentTimeMillis()}.json")
            putExtra(Intent.EXTRA_TEXT, jsonBackup)
            type = "application/json"
        }
        val chooser = Intent.createChooser(sendIntent, "Backup to Google Drive or other apps")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
