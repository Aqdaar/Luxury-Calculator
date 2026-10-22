package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray

/**
 * Room Entity storing calculation records formatted as Google Keep-style notes.
 */
@Entity(tableName = "calculations")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionName: String,
    val templateTag: String = "General",
    val formula: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val operand1Tag: String = "",
    val operand2Tag: String = "",
    val entryTagsJson: String = "",
    val cardColorHex: Long = 0x22FFFFFF,
    val isPinned: Boolean = false
) {
    fun getAllEntryTags(): List<String> {
        val list = mutableListOf<String>()
        if (entryTagsJson.isNotBlank()) {
            try {
                val array = JSONArray(entryTagsJson)
                for (i in 0 until array.length()) {
                    val tag = array.optString(i, "").trim()
                    if (tag.isNotEmpty()) list.add(tag)
                }
            } catch (_: Exception) {}
        }
        if (list.isEmpty()) {
            if (operand1Tag.isNotBlank() && operand1Tag != "unknown") list.add(operand1Tag.trim())
            if (operand2Tag.isNotBlank() && operand2Tag != "unknown") list.add(operand2Tag.trim())
        }
        return list
    }
}
