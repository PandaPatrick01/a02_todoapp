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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EveningClosureScreen(
    onFinish: () -> Unit
) {
    val backgroundColor = Color(0xFF2B2B36)
    val headlineColor = Color(0xFFF7F5F2)
    val accentColor = Color(0xFFFF9F43) // Accent Warm
    val dividerColor = Color(0xFFF7F5F2).copy(alpha = 0.2f)

    // Dummy Tasks für die Demo
    val tasks = listOf(
        "Meeting Protokoll senden",
        "Code Review abschließen",
        "Inbox Zero",
        "Dokumentation updaten"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        // 1. Headline (Top)
        Spacer(modifier = Modifier.height(60.dp))
        
        Text(
            text = "Was hast du heute geschafft?",
            color = headlineColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 34.sp,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        // 2. Task-List (Middle)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            tasks.forEach { taskText ->
                ClosureTaskRow(
                    text = taskText,
                    textColor = headlineColor,
                    accentColor = accentColor
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // 3. Divider
        HorizontalDivider(
            thickness = 1.dp,
            color = dividerColor
        )
        Spacer(modifier = Modifier.height(24.dp))

        // 4. Button
        Button(
            onClick = onFinish,
            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text(
                text = "Für morgen übernehmen",
                color = backgroundColor, // Using dark bg color for text on orange button for contrast
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ClosureTaskRow(
    text: String,
    textColor: Color,
    accentColor: Color
) {
    val rowBackgroundColor = Color(0xFF1E1E24)
    var isChecked by remember { mutableStateOf(false) }
    
    // Animation State
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isChecked) {
        if (isChecked) {
            // Scale 0.8 -> 1.0 -> 0.95 -> 1.0 within 0.25s (250ms)
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
            .fillMaxWidth()
            .background(rowBackgroundColor, RoundedCornerShape(20.dp))
            .clickable { 
                if (!isChecked) {
                    SoundUtils.playSuccessSound()
                }
                isChecked = !isChecked 
            } // Toggle check state
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        if (isChecked) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "Done",
                tint = accentColor,
                modifier = Modifier
                    .size(24.dp)
                    .scale(scale.value)
            )
        } else {
            // Placeholder space
            Box(modifier = Modifier.size(24.dp))
        }
    }
}
