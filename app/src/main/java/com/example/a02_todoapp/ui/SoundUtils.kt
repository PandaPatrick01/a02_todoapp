package com.example.a02_todoapp.ui

import android.media.AudioManager
import android.media.ToneGenerator

object SoundUtils {
    // Singleton ToneGenerator um Ressourcen zu sparen
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    fun playSuccessSound() {
        // TONE_PROP_BEEP ist okay, aber TONE_SUP_CONFIRM ist oft freundlicher.
        // Wir probieren TONE_SUP_PIP oder TONE_CDMA_ALERT_CALL_GUARD für "ding"
        // TONE_PROP_ACK ist oft ein kurzes "Ping".
        try {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK) 
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
