package com.example.a02_todoapp.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MorningConfirmationScreen(
    selection: String,
    onFinish: () -> Unit
) {
    // Style vom MorningIntroScreen
    val darkColor = Color(0xFF2B2B36)
    val warmYellow = Color(0xFFC7A675)

    val gradient = Brush.verticalGradient(
        colors = listOf(darkColor, warmYellow)
    )

    // Nachricht basierend auf Auswahl
    val message = when (selection) {
        "Großer Schritt" -> "Mut wird heute belohnt."
        "Konstant bleiben" -> "Beständigkeit ist der Schlüssel."
        "Sauber abschließen" -> "Klarheit bringt neue Energie."
        else -> "Fokus gesetzt."
    }

    // Pulse Animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Anzeigedauer: 2.5 Sekunden, dann weiter
    LaunchedEffect(Unit) {
        delay(2500)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradient),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color(0xFFFFE3B7),
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .alpha(alpha)
        )
    }
}
