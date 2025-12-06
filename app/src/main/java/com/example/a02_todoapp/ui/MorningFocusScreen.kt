package com.example.a02_todoapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MorningFocusScreen(
    onFocusSelected: (String) -> Unit
) {
    val backgroundColor = Color(0xFFF7F5F2)
    val headerColor = Color(0xFF1E1E24)
    // Farbe vom MorningIntroScreen Hintergrund (Gradient-Ende oder Mischung)
    // Intro Gradient war: Dark(0xFF2B2B36) -> WarmYellow(0xFFC7A675)
    // Wir nutzen hier das WarmYellow für die Buttons, damit es harmonisch aber lesbar bleibt
    val buttonColor = Color(0xFFC7A675) 
    val buttonTextColor = Color(0xFF1E1E24)
    val footerColor = Color(0xFF2B2B36).copy(alpha = 0.6f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Header (Top 20% roughly)
        Spacer(modifier = Modifier.weight(0.2f))
        
        Text(
            text = "Worauf setzt du heute deinen Fokus?",
            color = headerColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 34.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // 2. Fokus-Buttons (Middle)
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val buttons = listOf("Großer Schritt", "Konstant bleiben", "Sauber abschließen")
            
            buttons.forEach { text ->
                FocusButton(
                    text = text,
                    backgroundColor = buttonColor,
                    textColor = buttonTextColor,
                    onClick = { onFocusSelected(text) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // 3. Footer (Bottom)
        Box(
            modifier = Modifier.weight(0.2f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Du setzt den Ton für deinen Tag.",
                color = footerColor,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun FocusButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    // Um den Gradienten-Look exakt zu imitieren, könnten wir einen Brush nutzen.
    // Aber da "gleiche Farbe" gefordert war und Buttons meist solid sind, 
    // bleiben wir bei der Solid Color #C7A675, die wir oben definiert haben.
    // Falls ein echter Gradient im Button gewünscht ist, müsste man hier Box mit background(brush) nutzen.
    
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth(0.86f)
            .height(64.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0xFFE0C090))
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
