package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Luxury Calculator", appName)
  }

  @Test
  fun testBackupExportAndImport() {
    val calc = com.example.data.db.CalculationEntity(
      id = 1,
      sessionName = "Grocery Split",
      templateTag = "Grocery",
      formula = "65 × 335",
      result = "21,755",
      operand1Tag = "boxes",
      operand2Tag = "people"
    )
    val json = com.example.data.backup.BackupManager.exportToJson(listOf(calc))
    val parsed = com.example.data.backup.BackupManager.parseFromJson(json)
    assertEquals(1, parsed.size)
    assertEquals("Grocery Split", parsed[0].sessionName)
    assertEquals("21,755", parsed[0].result)
    assertEquals("boxes", parsed[0].operand1Tag)
    assertEquals("people", parsed[0].operand2Tag)
  }
}
