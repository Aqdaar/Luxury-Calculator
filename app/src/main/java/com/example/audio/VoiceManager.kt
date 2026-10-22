package com.example.audio

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.accessibility.AccessibilityManager
import java.util.Locale

/**
 * TalkBack voice manager providing spoken audio accessibility feedback
 * for blind and visually impaired users.
 */
class VoiceManager(context: Context) : TextToSpeech.OnInitListener {
    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    var isInitialized = false
        private set

    var isTalkBackEnabled: Boolean = false

    private val accessibilityManager =
        appContext.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager

    init {
        initTts()
    }

    private fun initTts() {
        try {
            tts = TextToSpeech(appContext, this)
        } catch (e: Exception) {
            Log.e("VoiceManager", "Error initializing TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.setLanguage(Locale.US)
                }
                engine.setPitch(1.0f)
                engine.setSpeechRate(1.05f)
                isInitialized = true
                Log.d("VoiceManager", "TalkBack TTS Initialized Successfully")
            }
        } else {
            Log.e("VoiceManager", "TTS initialization failed with status $status")
        }
    }

    fun isSystemTalkBackActive(): Boolean {
        val am = accessibilityManager ?: return false
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN)
        return enabledServices.isNotEmpty() || am.isTouchExplorationEnabled
    }

    fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("VoiceManager", "Cannot open accessibility settings", e)
        }
    }

    fun speakButton(label: String) {
        if (!isTalkBackEnabled && !isSystemTalkBackActive()) return
        if (!isInitialized) return

        val speechText = when (label) {
            "C" -> "Clear all"
            "%" -> "Percent"
            "⌫" -> "Backspace"
            "÷" -> "Divided by"
            "×" -> "Multiplied by"
            "-" -> "Minus"
            "+" -> "Plus"
            "=" -> "Equals"
            "." -> "Point"
            "+/-" -> "Toggle sign"
            else -> label
        }
        speakInternal(speechText, TextToSpeech.QUEUE_FLUSH)
    }

    fun speakResult(resultText: String) {
        if (!isTalkBackEnabled && !isSystemTalkBackActive()) return
        if (!isInitialized) return

        val speechText = when {
            resultText.contains("Error", ignoreCase = true) -> "Error: Cannot divide by zero"
            resultText.isEmpty() -> "Zero"
            else -> "Result: $resultText"
        }
        speakInternal(speechText, TextToSpeech.QUEUE_ADD)
    }

    fun speakAnnouncement(text: String) {
        if (!isTalkBackEnabled && !isSystemTalkBackActive()) return
        if (!isInitialized) return
        speakInternal(text, TextToSpeech.QUEUE_FLUSH)
    }

    private fun speakInternal(text: String, queueMode: Int) {
        try {
            tts?.speak(text, queueMode, null, "TalkBack_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("VoiceManager", "TTS speak failed", e)
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
