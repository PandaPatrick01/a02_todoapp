package com.example.a02_todoapp.ui

import android.media.AudioManager
import android.media.ToneGenerator

object SoundUtils {
    // Singleton ToneGenerator um Ressourcen zu sparen
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    fun playSuccessSound() {
        try {
            // HIER KANNST DU VERSCHIEDENE TÖNE TESTEN:
            
            // 1. TONE_SUP_CONFIRM (Aktuell) -> Standard Bestätigung "Ding"
            // 2. TONE_CDMA_ALERT_CALL_GUARD -> Oft ein weicheres, melodisches "Tü-ding"
            // 3. TONE_PROP_ACK -> Ein kurzes, hohes "Ping" (wie ein Sonar)
            // 4. TONE_SUP_PIP -> Sehr kurzes "Pip"
            // 5. TONE_CDMA_CONFIRM -> Alternative Bestätigung
            // 6. TONE_SUP_RADIO_ACK -> Funkgerät-Bestätigung
            
            // Ändere die Konstante hier unten, um zu testen:
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
