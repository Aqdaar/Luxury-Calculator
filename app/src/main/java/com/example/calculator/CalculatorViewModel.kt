package com.example.calculator

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AmbientSoundManager
import com.example.audio.SoundscapeMode
import com.example.audio.VoiceManager
import com.example.data.db.AppDatabase
import com.example.data.db.CalculationEntity
import com.example.data.repository.CalculationRepository
import com.example.ui.theme.WallpaperStyle
import org.json.JSONArray
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val displayText: String = "0",
    val formulaText: String = "",
    val activeOperator: String? = null,
    val sessionName: String = "General Calculation",
    val templateTag: String = "General",
    val isCalculationSuccess: Boolean = false,
    val isTalkBackEnabled: Boolean = false,
    val isAmbientEnabled: Boolean = false,
    val soundscapeMode: SoundscapeMode = SoundscapeMode.ZEN_BOWL,
    val ambientVolume: Float = 0.5f,
    val isHapticsEnabled: Boolean = true,
    val wallpaperStyle: WallpaperStyle = WallpaperStyle.OBSIDIAN_GOLD,
    val accentColor: Color = Color(0xFFFFB800),
    val showOnboarding: Boolean = false,
    val showKeepHistory: Boolean = false,
    val showTalkBackSettings: Boolean = false,
    val showWallpaperThemeSettings: Boolean = false,
    val showSessionNameDialog: Boolean = false,
    // Interactive Operand Sub-Tagging
    val firstOperand: String? = null,
    val secondOperand: String? = null,
    val firstOperandTag: String? = null,
    val secondOperandTag: String? = null,
    val showOperandTagDialog: Boolean = false,
    val editingOperandIndex: Int = 1,
    val editingOperandValue: String = "",
    val customTags: List<String> = emptyList(),
    val dynamicSuggestions: List<String> = emptyList()
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {
    private val engine = CalculatorEngine()
    private val prefs = application.getSharedPreferences("luxury_calc_prefs", Context.MODE_PRIVATE)

    private val database = AppDatabase.getDatabase(application)
    private val repository = CalculationRepository(database.calculationDao())

    val voiceManager = VoiceManager(application)
    val ambientManager = AmbientSoundManager()

    val savedCalculations: StateFlow<List<CalculationEntity>> = repository.allCalculations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private val _successAnimationPulse = MutableSharedFlow<Unit>()
    val successAnimationPulse: SharedFlow<Unit> = _successAnimationPulse.asSharedFlow()

    // Internal calculation state registers
    private var rawInput: String = "0"
    private var storedOperand: String? = null
    private var pendingOperator: String? = null
    private var currentFormulaBuilder: String = ""
    private var isNewInputExpected: Boolean = false

    private var firstOpTag: String? = null
    private var secondOpTag: String? = null
    private val currentEntryTags = mutableListOf<String>()
    private val recentTags = mutableListOf<String>()

    init {
        val hasCompletedOnboarding = prefs.getBoolean("has_completed_onboarding", false)
        val savedTalkBack = prefs.getBoolean("is_talkback_enabled", false)
        val savedAmbient = prefs.getBoolean("is_ambient_enabled", false)
        val savedHaptics = prefs.getBoolean("is_haptics_enabled", true)
        val savedWallpaperName = prefs.getString("wallpaper_style", WallpaperStyle.OBSIDIAN_GOLD.name)
        val savedWallpaper = try {
            WallpaperStyle.valueOf(savedWallpaperName ?: WallpaperStyle.OBSIDIAN_GOLD.name)
        } catch (_: Exception) {
            WallpaperStyle.OBSIDIAN_GOLD
        }
        val savedColorArgb = prefs.getInt("accent_color_argb", Color(0xFFFFB800).toArgb())
        val savedAccentColor = Color(savedColorArgb)
        val savedModeName = prefs.getString("soundscape_mode", SoundscapeMode.ZEN_BOWL.name)
        val savedMode = try {
            SoundscapeMode.valueOf(savedModeName ?: SoundscapeMode.ZEN_BOWL.name)
        } catch (_: Exception) {
            SoundscapeMode.ZEN_BOWL
        }
        val savedVolume = prefs.getFloat("ambient_volume", 0.5f)

        // Load recent tags for dynamic suggestions
        val savedRecent = prefs.getString("recent_tags", "") ?: ""
        if (savedRecent.isNotBlank()) {
            recentTags.addAll(savedRecent.split(",").map { it.trim() }.filter { it.isNotBlank() })
        }

        // Load category tags (allows deleting original default tags: Grocery, Fuel, Mortgage, Dining, General)
        val defaultCategories = listOf("Grocery", "Fuel", "Mortgage", "Dining", "General")
        val savedCategories = prefs.getString("category_tags", null)
        val categoryList = if (savedCategories != null) {
            if (savedCategories.isBlank()) emptyList() else savedCategories.split(",").map { it.trim() }.filter { it.isNotBlank() }
        } else {
            val legacyCustom = prefs.getString("user_custom_tags", "") ?: ""
            if (legacyCustom.isNotBlank()) {
                (defaultCategories + legacyCustom.split(",").map { it.trim() }).distinct().filter { it.isNotBlank() }
            } else {
                defaultCategories
            }
        }

        voiceManager.isTalkBackEnabled = savedTalkBack
        ambientManager.isAmbientEnabled = savedAmbient
        ambientManager.selectedMode = savedMode
        ambientManager.volume = savedVolume

        val initialSuggestions = computeDynamicSuggestions(emptyList())

        _uiState.update {
            it.copy(
                isTalkBackEnabled = savedTalkBack,
                isAmbientEnabled = savedAmbient,
                soundscapeMode = savedMode,
                ambientVolume = savedVolume,
                isHapticsEnabled = savedHaptics,
                wallpaperStyle = savedWallpaper,
                accentColor = savedAccentColor,
                customTags = categoryList,
                dynamicSuggestions = initialSuggestions,
                showOnboarding = !hasCompletedOnboarding,
                showSessionNameDialog = hasCompletedOnboarding
            )
        }

        // Dynamically recompute top 8 suggestions based on frequency of use and last used whenever database updates
        viewModelScope.launch {
            repository.allCalculations.collect { calcs ->
                val updatedSuggestions = computeDynamicSuggestions(calcs)
                _uiState.update { it.copy(dynamicSuggestions = updatedSuggestions) }
            }
        }
    }

    fun onButtonPress(label: String) {
        if (_uiState.value.isHapticsEnabled) {
            triggerHapticFeedback()
        }
        ambientManager.playClick()
        voiceManager.speakButton(label)

        when (label) {
            "C" -> handleClear()
            "⌫" -> handleBackspace()
            "%" -> handlePercent()
            "+/-" -> handleToggleSign()
            "÷", "×", "-", "+" -> handleOperator(label)
            "=" -> handleEquals()
            "." -> handleDecimal()
            else -> handleDigit(label)
        }
    }

    private fun handleDigit(digit: String) {
        if (isNewInputExpected || rawInput == "0" || rawInput.contains("Error")) {
            rawInput = digit
            isNewInputExpected = false
        } else {
            if (rawInput.length < 15) {
                rawInput += digit
            }
        }
        updateDisplay()
    }

    private fun handleDecimal() {
        if (isNewInputExpected || rawInput.contains("Error")) {
            rawInput = "0."
            isNewInputExpected = false
        } else if (!rawInput.contains(".")) {
            rawInput += "."
        }
        updateDisplay()
    }

    private fun handleOperator(op: String) {
        if (storedOperand != null && pendingOperator != null && !isNewInputExpected) {
            // Chained operation - record tags of current operand
            firstOpTag?.let { if (it.isNotBlank() && it != "unknown" && !currentEntryTags.contains(it)) currentEntryTags.add(it) }
            secondOpTag?.let { if (it.isNotBlank() && it != "unknown" && !currentEntryTags.contains(it)) currentEntryTags.add(it) }

            val result = engine.calculate(storedOperand!!, rawInput, pendingOperator!!)
            when (result) {
                is CalculationResult.Success -> {
                    currentFormulaBuilder = "${engine.formatNumber(result.value)} $op "
                    storedOperand = result.value
                    rawInput = result.value
                    updateDisplay()
                }
                is CalculationResult.Error -> {
                    rawInput = result.message
                    storedOperand = null
                    pendingOperator = null
                    currentFormulaBuilder = ""
                    updateDisplay()
                    return
                }
            }
        } else {
            storedOperand = rawInput
            currentFormulaBuilder = "${engine.formatNumber(rawInput)} $op "
            firstOpTag?.let { if (it.isNotBlank() && it != "unknown" && !currentEntryTags.contains(it)) currentEntryTags.add(it) }
        }

        pendingOperator = op
        isNewInputExpected = true

        _uiState.update {
            it.copy(
                activeOperator = op,
                formulaText = currentFormulaBuilder,
                firstOperand = engine.formatNumber(storedOperand ?: rawInput),
                secondOperand = null,
                firstOperandTag = firstOpTag ?: "unknown",
                secondOperandTag = null
            )
        }
    }

    private fun handleEquals() {
        val op = pendingOperator
        val first = storedOperand
        if (op != null && first != null) {
            val second = rawInput
            val fullFormula = "${engine.formatNumber(first)} $op ${engine.formatNumber(second)}"
            val result = engine.calculate(first, second, op)
            when (result) {
                is CalculationResult.Success -> {
                    rawInput = result.value
                    storedOperand = null
                    pendingOperator = null
                    isNewInputExpected = true
                    currentFormulaBuilder = "$fullFormula ="

                    val finalOp1Tag = firstOpTag ?: "unknown"
                    val finalOp2Tag = secondOpTag ?: "unknown"

                    val allTags = mutableListOf<String>()
                    allTags.addAll(currentEntryTags)
                    if (finalOp1Tag.isNotBlank() && finalOp1Tag != "unknown" && !allTags.contains(finalOp1Tag)) {
                        allTags.add(finalOp1Tag)
                    }
                    if (finalOp2Tag.isNotBlank() && finalOp2Tag != "unknown" && !allTags.contains(finalOp2Tag)) {
                        allTags.add(finalOp2Tag)
                    }
                    val tagsToSave = if (allTags.isNotEmpty()) allTags else listOf(finalOp1Tag, finalOp2Tag)

                    updateDisplay(isSuccess = true, formula = currentFormulaBuilder)
                    ambientManager.playCalculationSuccess()
                    voiceManager.speakResult(result.value)

                    // Automatically save calculation to Room with ALL operand tags!
                    saveCalculationRecord(
                        formula = fullFormula,
                        result = engine.formatNumber(result.value),
                        op1Tag = finalOp1Tag,
                        op2Tag = finalOp2Tag,
                        allTags = tagsToSave
                    )

                    _uiState.update {
                        it.copy(
                            firstOperand = engine.formatNumber(first),
                            secondOperand = engine.formatNumber(second),
                            firstOperandTag = finalOp1Tag,
                            secondOperandTag = finalOp2Tag
                        )
                    }

                    viewModelScope.launch {
                        _successAnimationPulse.emit(Unit)
                    }
                }
                is CalculationResult.Error -> {
                    rawInput = result.message
                    storedOperand = null
                    pendingOperator = null
                    isNewInputExpected = true
                    currentFormulaBuilder = "$fullFormula = Error"
                    updateDisplay(formula = currentFormulaBuilder)
                    voiceManager.speakAnnouncement("Cannot divide by zero")
                }
            }
        } else {
            updateDisplay(isSuccess = true)
            ambientManager.playCalculationSuccess()
            voiceManager.speakResult(rawInput)
        }
        _uiState.update { it.copy(activeOperator = null) }
    }

    private fun handlePercent() {
        rawInput = engine.applyPercent(rawInput)
        updateDisplay()
    }

    private fun handleToggleSign() {
        rawInput = engine.toggleSign(rawInput)
        updateDisplay()
    }

    private fun handleClear() {
        rawInput = "0"
        storedOperand = null
        pendingOperator = null
        currentFormulaBuilder = ""
        isNewInputExpected = false
        firstOpTag = null
        secondOpTag = null
        currentEntryTags.clear()

        _uiState.update {
            it.copy(
                displayText = "0",
                formulaText = "",
                activeOperator = null,
                isCalculationSuccess = false,
                firstOperand = null,
                secondOperand = null,
                firstOperandTag = null,
                secondOperandTag = null
            )
        }
    }

    private fun handleBackspace() {
        if (isNewInputExpected || rawInput.contains("Error")) {
            rawInput = "0"
            isNewInputExpected = false
        } else {
            if (rawInput.length > 1) {
                rawInput = rawInput.dropLast(1)
                if (rawInput == "-") rawInput = "0"
            } else {
                rawInput = "0"
            }
        }
        updateDisplay()
    }

    private fun updateDisplay(isSuccess: Boolean = false, formula: String = currentFormulaBuilder) {
        val formatted = engine.formatNumber(rawInput)
        val op2 = if (pendingOperator != null && !isNewInputExpected) formatted else _uiState.value.secondOperand

        _uiState.update {
            it.copy(
                displayText = formatted,
                formulaText = formula,
                isCalculationSuccess = isSuccess,
                secondOperand = op2,
                firstOperand = if (storedOperand != null) engine.formatNumber(storedOperand!!) else it.firstOperand ?: formatted
            )
        }
    }

    // Open tag dialog for an operand
    fun openOperandTagDialog(index: Int) {
        val value = if (index == 1) {
            _uiState.value.firstOperand ?: _uiState.value.displayText
        } else {
            _uiState.value.secondOperand ?: _uiState.value.displayText
        }

        _uiState.update {
            it.copy(
                showOperandTagDialog = true,
                editingOperandIndex = index,
                editingOperandValue = value
            )
        }
    }

    fun closeOperandTagDialog() {
        _uiState.update { it.copy(showOperandTagDialog = false) }
    }

    fun assignOperandTag(index: Int, tag: String) {
        val trimmed = tag.trim()
        trackUsedTag(trimmed)
        if (index == 1) {
            firstOpTag = trimmed
            if (trimmed.isNotBlank() && trimmed != "unknown" && !currentEntryTags.contains(trimmed)) {
                currentEntryTags.add(trimmed)
            }
            _uiState.update {
                it.copy(
                    firstOperandTag = trimmed,
                    showOperandTagDialog = false
                )
            }
            voiceManager.speakAnnouncement("Tagged ${_uiState.value.firstOperand ?: ""} as $trimmed")
        } else {
            secondOpTag = trimmed
            if (trimmed.isNotBlank() && trimmed != "unknown" && !currentEntryTags.contains(trimmed)) {
                currentEntryTags.add(trimmed)
            }
            _uiState.update {
                it.copy(
                    secondOperandTag = trimmed,
                    showOperandTagDialog = false
                )
            }
            voiceManager.speakAnnouncement("Tagged ${_uiState.value.secondOperand ?: ""} as $trimmed")
        }
    }

    fun clearOperandTag(index: Int) {
        if (index == 1) {
            firstOpTag = null
            _uiState.update {
                it.copy(
                    firstOperandTag = "unknown",
                    showOperandTagDialog = false
                )
            }
        } else {
            secondOpTag = null
            _uiState.update {
                it.copy(
                    secondOperandTag = "unknown",
                    showOperandTagDialog = false
                )
            }
        }
    }

    private fun trackUsedTag(tag: String) {
        val trimmed = tag.trim()
        if (trimmed.isBlank() || trimmed.equals("unknown", ignoreCase = true)) return
        recentTags.remove(trimmed)
        recentTags.add(0, trimmed) // Put last used at the front
        while (recentTags.size > 20) {
            recentTags.removeAt(recentTags.size - 1)
        }
        prefs.edit().putString("recent_tags", recentTags.joinToString(",")).apply()
        val suggestions = computeDynamicSuggestions(savedCalculations.value)
        _uiState.update { it.copy(dynamicSuggestions = suggestions) }
    }

    private fun computeDynamicSuggestions(calculations: List<CalculationEntity>): List<String> {
        val frequencyMap = mutableMapOf<String, Int>()

        // 1. Tally frequency from saved calculations in Keep
        calculations.forEach { calc ->
            calc.getAllEntryTags().forEach { tag ->
                val clean = tag.trim()
                if (clean.isNotBlank() && !clean.equals("unknown", ignoreCase = true)) {
                    frequencyMap[clean] = (frequencyMap[clean] ?: 0) + 1
                }
            }
            val cat = calc.templateTag.trim()
            if (cat.isNotBlank() && cat != "General" && cat != "All") {
                frequencyMap[cat] = (frequencyMap[cat] ?: 0) + 1
            }
        }

        // 2. Weight recent tags so last used is highest priority
        recentTags.forEachIndexed { index, tag ->
            val recencyWeight = (recentTags.size - index) * 5
            frequencyMap[tag] = (frequencyMap[tag] ?: 0) + recencyWeight
        }

        // 3. Fallback baseline tags if user hasn't accumulated many tags yet
        val defaults = listOf("boxes", "people", "hours", "rate", "fuel", "tax", "units", "items")
        defaults.forEach { tag ->
            if (!frequencyMap.containsKey(tag)) {
                frequencyMap[tag] = 0
            }
        }

        // 4. Return top 8 dynamically changing based on frequency of use & last used
        return frequencyMap.entries
            .sortedWith(
                compareByDescending<Map.Entry<String, Int>> { it.value }
                    .thenBy { recentTags.indexOf(it.key).let { idx -> if (idx >= 0) idx else 999 } }
            )
            .map { it.key }
            .distinct()
            .take(8)
    }

    private fun saveCalculationRecord(
        formula: String,
        result: String,
        op1Tag: String,
        op2Tag: String,
        allTags: List<String>
    ) {
        viewModelScope.launch {
            val tagsJsonArray = JSONArray()
            allTags.forEach { tagsJsonArray.put(it) }

            val record = CalculationEntity(
                sessionName = _uiState.value.sessionName,
                templateTag = _uiState.value.templateTag,
                formula = formula,
                result = result,
                operand1Tag = op1Tag,
                operand2Tag = op2Tag,
                entryTagsJson = tagsJsonArray.toString(),
                timestamp = System.currentTimeMillis()
            )
            repository.save(record)
        }
    }

    fun callBackCalculation(entity: CalculationEntity) {
        val cleanResult = entity.result.replace(",", "")
        rawInput = cleanResult
        storedOperand = null
        pendingOperator = null
        isNewInputExpected = true
        currentFormulaBuilder = "${entity.formula} ="
        currentEntryTags.clear()

        val allTags = entity.getAllEntryTags()
        currentEntryTags.addAll(allTags)

        firstOpTag = entity.operand1Tag.ifBlank { null }
        secondOpTag = entity.operand2Tag.ifBlank { null }

        _uiState.update {
            it.copy(
                displayText = entity.result,
                formulaText = currentFormulaBuilder,
                sessionName = entity.sessionName,
                templateTag = entity.templateTag,
                firstOperand = entity.result,
                firstOperandTag = firstOpTag ?: "unknown",
                secondOperand = null,
                secondOperandTag = null,
                showKeepHistory = false
            )
        }
        voiceManager.speakAnnouncement("Loaded calculation ${entity.sessionName}: ${entity.result}")
    }

    fun updateCalculation(entity: CalculationEntity) {
        viewModelScope.launch {
            repository.update(entity)
        }
    }

    fun deleteCalculation(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

    fun deleteCalculations(ids: Set<Long>) {
        viewModelScope.launch {
            repository.deleteMultiple(ids)
        }
    }

    fun addCustomTag(tag: String) {
        val trimmed = tag.trim()
        val current = _uiState.value.customTags.toMutableList()
        if (trimmed.isNotBlank() && !current.contains(trimmed)) {
            current.add(trimmed)
            saveCategoryTags(current)
            _uiState.update { it.copy(customTags = current) }
        }
    }

    fun deleteCustomTag(tag: String) {
        val trimmed = tag.trim()
        val current = _uiState.value.customTags.toMutableList()
        current.remove(trimmed)
        saveCategoryTags(current)
        _uiState.update { it.copy(customTags = current) }
    }

    private fun saveCategoryTags(tags: List<String>) {
        prefs.edit().putString("category_tags", tags.joinToString(",")).apply()
    }

    fun startNamedSession(name: String, category: String) {
        _uiState.update {
            it.copy(
                sessionName = name,
                templateTag = category,
                showSessionNameDialog = false
            )
        }
        voiceManager.speakAnnouncement("Starting $name")
    }

    fun setWallpaper(style: WallpaperStyle) {
        prefs.edit().putString("wallpaper_style", style.name).apply()
        _uiState.update { it.copy(wallpaperStyle = style) }
    }

    fun setAccentColor(color: Color) {
        prefs.edit().putInt("accent_color_argb", color.toArgb()).apply()
        _uiState.update { it.copy(accentColor = color) }
    }

    fun toggleTalkBack(enabled: Boolean) {
        voiceManager.isTalkBackEnabled = enabled
        prefs.edit().putBoolean("is_talkback_enabled", enabled).apply()
        _uiState.update { it.copy(isTalkBackEnabled = enabled) }
        if (enabled) {
            voiceManager.speakAnnouncement("TalkBack accessibility enabled")
        }
    }

    fun toggleAmbient(enabled: Boolean) {
        ambientManager.isAmbientEnabled = enabled
        prefs.edit().putBoolean("is_ambient_enabled", enabled).apply()
        _uiState.update { it.copy(isAmbientEnabled = enabled) }
    }

    fun setSoundscapeMode(mode: SoundscapeMode) {
        ambientManager.selectedMode = mode
        prefs.edit().putString("soundscape_mode", mode.name).apply()
        _uiState.update { it.copy(soundscapeMode = mode) }
    }

    fun setAmbientVolume(volume: Float) {
        ambientManager.volume = volume
        prefs.edit().putFloat("ambient_volume", volume).apply()
        _uiState.update { it.copy(ambientVolume = volume) }
    }

    fun toggleHaptics(enabled: Boolean) {
        prefs.edit().putBoolean("is_haptics_enabled", enabled).apply()
        _uiState.update { it.copy(isHapticsEnabled = enabled) }
    }

    fun completeOnboarding() {
        prefs.edit().putBoolean("has_completed_onboarding", true).apply()
        _uiState.update {
            it.copy(
                showOnboarding = false,
                showSessionNameDialog = true
            )
        }
    }

    fun showKeepHistory(show: Boolean) {
        _uiState.update { it.copy(showKeepHistory = show) }
    }

    fun showTalkBackSettings(show: Boolean) {
        _uiState.update { it.copy(showTalkBackSettings = show) }
    }

    fun showWallpaperThemeSettings(show: Boolean) {
        _uiState.update { it.copy(showWallpaperThemeSettings = show) }
    }

    fun showSessionNameDialog(show: Boolean) {
        _uiState.update { it.copy(showSessionNameDialog = show) }
    }

    private fun triggerHapticFeedback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(18)
            }
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.shutdown()
        ambientManager.shutdown()
    }
}
