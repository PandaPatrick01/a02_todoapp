package com.example.a02_todoapp.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun TomorrowSetupScreen(
    onFinish: () -> Unit
) {
    // State, um zwischen Setup und Completion View zu wechseln
    var showCompletion by remember { mutableStateOf(false) }

    if (showCompletion) {
        CompletionView(onFinish = onFinish)
    } else {
        SetupView(onOptionSelected = { showCompletion = true })
    }
}

@Composable
fun SetupView(
    onOptionSelected: () -> Unit
) {
    val backgroundColor = Color(0xFFF7F5F2)
    val headlineColor = Color(0xFF1E1E24)
    val cardBackgroundColor = Color(0xFFFFF5E8)
    val buttonColor = Color(0xFFFFC46B) // Wie Morning Focus
    val buttonTextColor = Color(0xFF1E1E24)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Headline
        Spacer(modifier = Modifier.weight(0.2f))
        
        Text(
            text = "Wie soll morgen starten?",
            color = headlineColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        // 2. "Erster Schritt morgen" Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(cardBackgroundColor, shape = RoundedCornerShape(22.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Vorschläge
                val suggestions = listOf("Großer Start", "Ruhiger Start", "Konstanter Schritt")
                
                suggestions.forEach { text ->
                    Button(
                        onClick = onOptionSelected,
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0xFFE0C090))
                    ) {
                        Text(
                            text = text,
                            color = buttonTextColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@Composable
fun CompletionView(
    onFinish: () -> Unit
) {
    // Full-screen fade (dark -> warm yellow gradient)
    val darkColor = Color(0xFF2B2B36) 
    val warmYellow = Color(0xFFC7A675) // Etwas dunkleres Warm-Gelb für den Gradient-Auslauf
    
    val gradient = Brush.verticalGradient(
        colors = listOf(darkColor, warmYellow)
    )

    // Pulse Animation (Light glow pulse - 1s duration)
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

    // Automatisch beenden nach z.B. 3 Sekunden (Simuliert das Ende des Effekts)
    LaunchedEffect(Unit) {
        delay(3000)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradient),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Morgen beginnt mit Klarheit.",
            color = Color(0xFFFFE3B7),
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.alpha(alpha)
        )
    }
}
