package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

enum class SoundscapeMode(val displayName: String, val description: String) {
    ZEN_BOWL("Zen Singing Bowl", "Harmonic 432Hz deep meditative drone"),
    DEEP_FOCUS("Deep Focus", "Subtle binaural alpha-theta resonance"),
    CRYSTAL_RAIN("Crystal Rain", "Gentle acoustic droplet chimes")
}

/**
 * Procedural luxury soundscape synthesizer and tactile acoustic click generator.
 */
class AmbientSoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var ambientJob: Job? = null
    private var ambientTrack: AudioTrack? = null

    var isAmbientEnabled: Boolean = false
        set(value) {
            field = value
            if (value) restartAmbient() else stopAmbient()
        }

    var selectedMode: SoundscapeMode = SoundscapeMode.ZEN_BOWL
        set(value) {
            field = value
            if (isAmbientEnabled) restartAmbient()
        }

    var volume: Float = 0.5f
        set(value) {
            field = value.coerceIn(0.0f, 1.0f)
            ambientTrack?.setVolume(field * 0.25f) // Master safe ceiling
        }

    var isClickSoundEnabled: Boolean = true

    private var clickTrack: AudioTrack? = null
    private var successTrack: AudioTrack? = null

    init {
        initSoundEffects()
    }

    private fun initSoundEffects() {
        try {
            val sampleRate = 22050
            // 1. Tactile glass-ceramic click
            val clickSamples = (sampleRate * 0.012).toInt()
            val clickBuffer = ShortArray(clickSamples)
            for (i in 0 until clickSamples) {
                val t = i.toDouble() / sampleRate
                val decay = (1.0 - (i.toDouble() / clickSamples))
                val freq = 1450.0 - (t * 90000.0)
                val sample = sin(2.0 * Math.PI * freq * t) * decay * decay * 13000.0
                clickBuffer[i] = sample.toInt().toShort()
            }

            clickTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(clickBuffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            clickTrack?.write(clickBuffer, 0, clickBuffer.size)

            // 2. Calculation completion harmonic chime
            val successSamples = (sampleRate * 0.22).toInt()
            val successBuffer = ShortArray(successSamples)
            for (i in 0 until successSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = (1.0 - (i.toDouble() / successSamples))
                val wave1 = sin(2.0 * Math.PI * 528.0 * t) // Solfeggio 528Hz Love/Clarity
                val wave2 = sin(2.0 * Math.PI * 792.0 * t) * 0.4
                val wave3 = sin(2.0 * Math.PI * 1056.0 * t) * 0.2
                val sample = (wave1 + wave2 + wave3) * envelope * 11000.0
                successBuffer[i] = sample.toInt().toShort()
            }

            successTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(successBuffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            successTrack?.write(successBuffer, 0, successBuffer.size)
        } catch (e: Exception) {
            Log.e("AmbientSoundManager", "Error generating click effects", e)
        }
    }

    fun playClick() {
        if (!isClickSoundEnabled) return
        try {
            clickTrack?.let { track ->
                track.stop()
                track.reloadStaticData()
                track.play()
            }
        } catch (e: Exception) {
            Log.e("AmbientSoundManager", "Failed to play click", e)
        }
    }

    fun playCalculationSuccess() {
        try {
            successTrack?.let { track ->
                track.stop()
                track.reloadStaticData()
                track.play()
            }
        } catch (e: Exception) {
            Log.e("AmbientSoundManager", "Failed to play success sound", e)
        }
    }

    private fun restartAmbient() {
        stopAmbient()
        startAmbient()
    }

    private fun startAmbient() {
        if (ambientJob?.isActive == true) return
        ambientJob = scope.launch {
            var localTrack: AudioTrack? = null
            try {
                val sampleRate = 22050
                val durationSec = 4
                val numSamples = sampleRate * durationSec
                val buffer = ShortArray(numSamples)

                when (selectedMode) {
                    SoundscapeMode.ZEN_BOWL -> {
                        // 432 Hz warm singing bowl tone with 0.2 Hz breath LFO
                        for (i in 0 until numSamples) {
                            val t = i.toDouble() / sampleRate
                            val lfo = 0.7 + 0.3 * sin(2.0 * Math.PI * 0.25 * t)
                            val f1 = sin(2.0 * Math.PI * 216.0 * t) * 0.45
                            val f2 = sin(2.0 * Math.PI * 432.0 * t) * 0.35
                            val f3 = sin(2.0 * Math.PI * 648.0 * t) * 0.15
                            val sample = (f1 + f2 + f3) * lfo * 3200.0
                            buffer[i] = sample.toInt().toShort()
                        }
                    }
                    SoundscapeMode.DEEP_FOCUS -> {
                        // Binaural theta pulse (108Hz carrier + 114Hz theta beat = 6Hz brainwave)
                        for (i in 0 until numSamples) {
                            val t = i.toDouble() / sampleRate
                            val carrier = sin(2.0 * Math.PI * 108.0 * t) * 0.4
                            val beat = sin(2.0 * Math.PI * 114.0 * t) * 0.4
                            val sub = sin(2.0 * Math.PI * 54.0 * t) * 0.2
                            val sample = (carrier + beat + sub) * 2800.0
                            buffer[i] = sample.toInt().toShort()
                        }
                    }
                    SoundscapeMode.CRYSTAL_RAIN -> {
                        // Soft crystalline drops over delicate velvet hum
                        val rand = Random(42)
                        for (i in 0 until numSamples) {
                            val t = i.toDouble() / sampleRate
                            val baseHum = sin(2.0 * Math.PI * 174.0 * t) * 0.2
                            // Occasional chime
                            val drop = if (i % (sampleRate / 3) < 400) {
                                val dt = (i % (sampleRate / 3)).toDouble() / sampleRate
                                sin(2.0 * Math.PI * 1320.0 * dt) * (1.0 - dt * 80.0).coerceAtLeast(0.0) * 0.25
                            } else 0.0
                            val sample = (baseHum + drop) * 3000.0
                            buffer[i] = sample.toInt().toShort()
                        }
                    }
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                localTrack = track
                ambientTrack = track
                track.write(buffer, 0, buffer.size)
                track.setLoopPoints(0, buffer.size, -1)
                track.setVolume(volume * 0.25f)
                track.play()

                while (isActive && isAmbientEnabled) {
                    kotlinx.coroutines.delay(1000)
                }
            } catch (_: kotlinx.coroutines.CancellationException) {
                // Expected normal cancellation when pausing or switching soundscapes
            } catch (e: Exception) {
                Log.e("AmbientSoundManager", "Ambient loop error", e)
            } finally {
                cleanupTrack(localTrack)
            }
        }
    }

    private fun stopAmbient() {
        val job = ambientJob
        ambientJob = null
        job?.cancel()

        val track = ambientTrack
        ambientTrack = null
        cleanupTrack(track)
    }

    private fun cleanupTrack(trackToClean: AudioTrack?) {
        try {
            trackToClean?.let {
                if (it.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    it.stop()
                }
                it.release()
            }
            if (ambientTrack === trackToClean) {
                ambientTrack = null
            }
        } catch (_: Exception) {
            // Safely ignore audio track release exceptions
        }
    }

    fun shutdown() {
        stopAmbient()
        try {
            clickTrack?.release()
            clickTrack = null
            successTrack?.release()
            successTrack = null
        } catch (e: Exception) {
            Log.e("AmbientSoundManager", "Error during shutdown", e)
        }
    }
}
