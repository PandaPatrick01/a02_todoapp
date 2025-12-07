package com.example.a02_todoapp.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.a02_todoapp.data.local.WeeklyGoalViewModel

@Composable
fun PlanningWeeklyScreen(
    weeklyViewModel: WeeklyGoalViewModel,
    onNewWeekClick: () -> Unit // Wird nun eher "onGoalAdded" sein oder wir handlen alles hier
) {
    val backgroundColor = Color(0xFFF7F5F2)
    val cardColor = Color.White
    val textColor = Color(0xFF1E1E24)
    val accentColor = Color(0xFFFF9F43)

    // Echte Daten aus ViewModel
    val goals by weeklyViewModel.goals.collectAsState()

    var showNewWeekDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Wochenplanung",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Liste der Wochen (Mock)
            // Hier würde man normalerweise aus der DB alle angelegten Wochen holen.
            // Wir nutzen eine Dummy-Liste erweitert um die Einträge, die man vllt. erstellt.
            val weeks = listOf("2025 KW50", "2025 KW51") 
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(weeks) { weekTitle ->
                    WeekItem(
                        title = weekTitle, 
                        onClick = { onNewWeekClick() }, 
                        backgroundColor = cardColor, 
                        textColor = textColor
                    )
                }
            }
        }

        // Plus-Button für NEUE WOCHE
        FloatingActionButton(
            onClick = { showNewWeekDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp),
            containerColor = accentColor,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Neue Woche")
        }

        if (showNewWeekDialog) {
            NewWeekDialog(
                onDismiss = { showNewWeekDialog = false },
                onCreate = { year, kw ->
                    // Hier würde man die neue Woche im VM anlegen
                    showNewWeekDialog = false
                    // Navigieren zur Detailansicht dieser Woche
                    onNewWeekClick() 
                }
            )
        }
    }
}

@Composable
fun WeekItem(
    title: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

@Composable
fun NewWeekDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var year by remember { mutableStateOf("2025") }
    var kw by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Neue Woche planen",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Jahr") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                OutlinedTextField(
                    value = kw,
                    onValueChange = { kw = it },
                    label = { Text("Kalenderwoche (z.B. 52)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Abbrechen")
                    }
                    Button(
                        onClick = { onCreate(year, kw) },
                        enabled = year.isNotBlank() && kw.isNotBlank()
                    ) {
                        Text("Erstellen")
                    }
                }
            }
        }
    }
}
