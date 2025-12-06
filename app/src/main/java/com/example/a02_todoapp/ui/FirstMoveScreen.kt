package com.example.a02_todoapp.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun FirstMoveScreen(
    onAllCompleted: () -> Unit
) {
    val backgroundColor = Color(0xFFF7F5F2)
    val headerColor = Color(0xFF1E1E24)
    val footerColor = Color(0xFF2B2B36).copy(alpha = 0.5f)

    // State für die 3 Aufgaben
    var isTask1Done by remember { mutableStateOf(false) }
    var isTask2Done by remember { mutableStateOf(false) }
    var isTask3Done by remember { mutableStateOf(false) }

    // Wenn alle erledigt sind, kurz warten und dann weiterleiten
    LaunchedEffect(isTask1Done, isTask2Done, isTask3Done) {
        if (isTask1Done && isTask2Done && isTask3Done) {
            delay(500) // Kurze Verzögerung für die Wahrnehmung des letzten Hakens
            onAllCompleted()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Header (Top)
        Spacer(modifier = Modifier.weight(0.2f))
        
        Text(
            text = "Erster Move?",
            color = headerColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 34.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        // 2. Option-Buttons (Middle) -> Jetzt Checkable Rows
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FirstMoveCheckableButton(
                text = "Arbeitsplatz öffnen",
                isChecked = isTask1Done,
                onToggle = { 
                    if (!isTask1Done) SoundUtils.playSuccessSound()
                    isTask1Done = !isTask1Done 
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            FirstMoveCheckableButton(
                text = "90 Sekunden meditieren",
                isChecked = isTask2Done,
                onToggle = { 
                    if (!isTask2Done) SoundUtils.playSuccessSound()
                    isTask2Done = !isTask2Done 
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            FirstMoveCheckableButton(
                text = "Zwei Übungen zum Strecken",
                isChecked = isTask3Done,
                onToggle = { 
                    if (!isTask3Done) SoundUtils.playSuccessSound()
                    isTask3Done = !isTask3Done 
                }
            )
        }

        // 3. Footer (Bottom)
        Box(
            modifier = Modifier.weight(0.2f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nur ein Schritt zählt.",
                color = footerColor,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun FirstMoveCheckableButton(
    text: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    // Farben
    val buttonColor = Color(0xFFFF9F43) // Orange wie vorher
    val textColor = Color(0xFF1E1E24)
    val checkmarkColor = Color(0xFF1E1E24) // Dunkel für Kontrast auf Orange

    // Animation State (kopiert von EveningClosureScreen)
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isChecked) {
        if (isChecked) {
            scale.snapTo(0.8f)
            scale.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 250
                    0.8f at 0
                    1.0f at 100
                    0.95f at 180
                    1.0f at 250
                }
            )
        } else {
            scale.snapTo(1f)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(0.86f)
            .height(64.dp)
            .shadow(
                elevation = 4.dp, 
                shape = RoundedCornerShape(20.dp), 
                spotColor = Color(0xFFE0C090)
            )
            .background(buttonColor, shape = RoundedCornerShape(20.dp))
            .clickable { onToggle() }
            .padding(horizontal = 20.dp), // Padding links/rechts für Text/Icon Abstand
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        if (isChecked) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "Done",
                tint = checkmarkColor,
                modifier = Modifier
                    .size(28.dp) // Etwas größer als im Evening Screen
                    .scale(scale.value)
            )
        } else {
            // Platzhalter damit der Text nicht springt
            Box(modifier = Modifier.size(28.dp))
        }
    }
}
