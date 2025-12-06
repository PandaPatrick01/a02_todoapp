package com.example.a02_todoapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MiddayResetScreen(
    onTimerStart: () -> Unit
) {
    val backgroundColor = Color(0xFFEDE9E3)
    val headerColor = Color(0xFF1E1E24)
    val cardBackgroundColor = Color(0xFFF7F5F2)
    val chipBackgroundColor = Color(0xFFFFE3B7)
    val timerButtonColor = Color(0xFFFF9F43)
    
    // Mood Colors
    val moodGood = Color(0xFF67C587)
    val moodOkay = Color(0xFFFFC46B)
    val moodBad = Color(0xFFFF6B6B)

    // State to show panel
    var isMoodSelected by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 0.dp), // Card has specific margins, so we handle padding inside
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        // 1. Headline (Top spacing handled by spacer)
        Spacer(modifier = Modifier.height(60.dp)) // Approximate top spacing
        
        Text(
            text = "Wo stehst du gerade?",
            color = headerColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // 2. Mood-Buttons
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoodButton(color = moodGood, onClick = { isMoodSelected = true })
            Spacer(modifier = Modifier.width(20.dp))
            MoodButton(color = moodOkay, onClick = { isMoodSelected = true })
            Spacer(modifier = Modifier.width(20.dp))
            MoodButton(color = moodBad, onClick = { isMoodSelected = true })
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 3. Task Panel & 4. Timer Button (Appear after selection)
        AnimatedVisibility(
            visible = isMoodSelected,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Task Panel Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(22.dp), spotColor = Color(0xFFD0C0A0))
                        .background(cardBackgroundColor, shape = RoundedCornerShape(22.dp))
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Fokus-Aufgabe",
                            color = headerColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Projektpräsentation vorbereiten", // Placeholder Subtext
                            color = headerColor.copy(alpha = 0.7f),
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Mini-Step Chip
                        Box(
                            modifier = Modifier
                                .background(chipBackgroundColor, shape = RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Outline erstellen", // Placeholder Mini-Step
                                color = headerColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. 90-Sekunden-Timer Button
                Button(
                    onClick = onTimerStart,
                    colors = ButtonDefaults.buttonColors(containerColor = timerButtonColor),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(60.dp)
                ) {
                    Text(
                        text = "90 Sekunden Fokus",
                        color = headerColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun MoodButton(
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(78.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
    )
}
