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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a02_todoapp.Task
import com.example.a02_todoapp.data.local.ToDoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DayTaskScreen(
    viewModel: ToDoViewModel,
    onAllTasksDone: () -> Unit
) {
    val backgroundColor = Color(0xFFF7F5F2)
    val headerColor = Color(0xFF1E1E24)
    
    // Aufgaben aus dem ViewModel beobachten
    val allTasks by viewModel.tasks.collectAsState(initial = emptyList())
    
    // Wir zeigen nur Aufgaben an, die NICHT erledigt sind.
    val visibleTasks = allTasks.filter { !it.done }

    // Lokaler State für IDs, die gerade als "checked" animiert werden
    val animatingTaskIds = remember { mutableStateListOf<Long>() }
    
    val scope = rememberCoroutineScope()

    LaunchedEffect(visibleTasks.size) {
        if (visibleTasks.isEmpty() && allTasks.isNotEmpty()) {
             delay(500)
             onAllTasksDone()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Headline
        Spacer(modifier = Modifier.height(60.dp))
        Text(
            text = "Deine Fokus-Liste",
            color = headerColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Task Liste
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(items = visibleTasks, key = { _, item -> item.id }) { index, task ->
                // Nur die ersten 6 Items anzeigen
                if (index <= 5) {
                    val isTopItem = index == 0
                    
                    // Transparenz
                    val opacity = when (index) {
                        0 -> 1f
                        1 -> 0.6f
                        2 -> 0.4f
                        3 -> 0.3f
                        4 -> 0.2f
                        else -> 0.1f
                    }

                    // Ist diese Task gerade am Verschwinden?
                    val isAnimating = animatingTaskIds.contains(task.id)

                    TaskRow(
                        task = task,
                        opacity = opacity,
                        isTopItem = isTopItem, 
                        isAnimating = isAnimating,
                        onCheckRequest = {
                            if (!isAnimating) {
                                // Sound abspielen
                                SoundUtils.playSuccessSound()
                                
                                animatingTaskIds.add(task.id)
                                
                                scope.launch {
                                    // 750ms warten (Animation läuft im Row)
                                    delay(750) 
                                    // Dann in DB als erledigt markieren
                                    viewModel.toggle(task.id)
                                    animatingTaskIds.remove(task.id)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskRow(
    task: Task,
    opacity: Float,
    isTopItem: Boolean,
    isAnimating: Boolean,
    onCheckRequest: () -> Unit
) {
    // Farben
    val standardRowColor = Color(0xFF1E1E24)
    val topRowColor = Color(0xFFFF9F43) // Orange für Top Item
    val checkedRowColor = Color(0xFF1E1E24)
    
    val rowBackgroundColor = if (isTopItem && !isAnimating) topRowColor else standardRowColor
    val textColor = if (isTopItem && !isAnimating) Color(0xFF1E1E24) else Color(0xFFF7F5F2)
    val checkmarkTint = if (isTopItem && !isAnimating) Color(0xFF1E1E24) else Color(0xFFFF9F43)

    // Größenanpassung
    val minHeight = if (isTopItem) 180.dp else 60.dp 
    val fontSize = if (isTopItem) 26.sp else 18.sp
    
    // Animation Scale
    val scale = remember { Animatable(1f) }
    
    LaunchedEffect(isAnimating) {
        if (isAnimating) {
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
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .alpha(opacity)
            .background(rowBackgroundColor, RoundedCornerShape(20.dp))
            .then(if (isTopItem && !isAnimating) Modifier.clickable { onCheckRequest() } else Modifier)
            .padding(24.dp), 
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = task.title,
                color = if (isAnimating) textColor.copy(alpha = 0.5f) else textColor,
                fontSize = fontSize,
                fontWeight = FontWeight.SemiBold,
                textDecoration = if (isAnimating) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                maxLines = if (isTopItem) 3 else 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.size(16.dp))

            if (isAnimating) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Done",
                    tint = checkmarkTint,
                    modifier = Modifier
                        .size(32.dp)
                        .scale(scale.value)
                )
            } else if (isTopItem) {
                Box(modifier = Modifier.size(32.dp))
            }
        }
    }
}
