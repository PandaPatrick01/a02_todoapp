package com.example.a02_todoapp.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.window.Dialog
import com.example.a02_todoapp.Category
import com.example.a02_todoapp.CategoryPicker
import com.example.a02_todoapp.Task
import com.example.a02_todoapp.data.local.ToDoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun DayTaskScreen(
    viewModel: ToDoViewModel,
    onAllTasksDone: () -> Unit,
    onPlanningClick: () -> Unit // Neuer Callback
) {
    val backgroundColor = Color(0xFFF7F5F2)
    val headerTextColor = Color(0xFF1E1E24)
    val inactiveHeaderColor = Color(0xFF1E1E24).copy(alpha = 0.4f)
    
    // Aufgaben aus dem ViewModel
    val allTasks by viewModel.tasks.collectAsState(initial = emptyList())
    // Nur unerledigte anzeigen
    val visibleTasks = allTasks.filter { !it.done }

    val animatingTaskIds = remember { mutableStateListOf<Long>() }
    val scope = rememberCoroutineScope()
    
    var showAddDialog by remember { mutableStateOf(false) }

    // Wenn alle Tasks erledigt sind -> Weiter
    LaunchedEffect(visibleTasks.size) {
        if (visibleTasks.isEmpty() && allTasks.isNotEmpty()) {
             delay(500)
             onAllTasksDone()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header Menü
            Spacer(modifier = Modifier.height(55.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Planung klickbar machen
                Text(
                    text = "Planung",
                    color = inactiveHeaderColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onPlanningClick() }
                )
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "DailyTask",
                        color = headerTextColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    // Kleiner Dot oder Unterstrich als Indikator
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(4.dp)
                            .background(Color(0xFFFF9F43), shape = RoundedCornerShape(2.dp))
                    )
                }
                
                Text(
                    text = "Recap",
                    color = inactiveHeaderColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.Black.copy(alpha = 0.05f))
            
            // 2. Scrollbare Liste (Wheel Style)
            Box(modifier = Modifier.weight(1f)) {
                if (visibleTasks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Keine Aufgaben offen!", color = inactiveHeaderColor)
                    }
                } else {
                    WheelTaskList(
                        tasks = visibleTasks,
                        animatingTaskIds = animatingTaskIds,
                        onTaskClick = { task ->
                            if (!animatingTaskIds.contains(task.id)) {
                                SoundUtils.playSuccessSound()
                                animatingTaskIds.add(task.id)
                                scope.launch {
                                    delay(750)
                                    viewModel.toggle(task.id)
                                    animatingTaskIds.remove(task.id)
                                }
                            }
                        }
                    )
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { 
                viewModel.onInputChange("") // Reset input
                showAddDialog = true 
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 52.dp, end = 24.dp), 
            containerColor = Color(0xFFFF9F43), // Akzentfarbe von oben
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add Task")
        }

        if (showAddDialog) {
            Dialog(onDismissRequest = { showAddDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Neue Aufgabe", style = MaterialTheme.typography.titleLarge)
                        
                        CategoryPicker(
                            selected = viewModel.selectedCategory,
                            onSelect = viewModel::onCategoryChange,
                            categories = Category.values().toList(),
                            modifier = Modifier.fillMaxWidth() 
                        )
                        
                        OutlinedTextField(
                            value = viewModel.input,
                            onValueChange = viewModel::onInputChange,
                            label = { Text("Aufgabe") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showAddDialog = false }) {
                                Text("Abbrechen")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    viewModel.add()
                                    showAddDialog = false
                                },
                                enabled = viewModel.input.isNotBlank()
                            ) {
                                Text("Hinzufügen")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WheelTaskList(
    tasks: List<Task>,
    animatingTaskIds: List<Long>,
    onTaskClick: (Task) -> Unit
) {
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    
    // Wir nutzen BoxWithConstraints um die exakte Höhe zu kennen
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val height = maxHeight
        val itemHeight = 300.dp
        
        // Damit das erste Item genau zentriert ist, muss das Padding oben/unten
        // genau (ContainerHöhe - ItemHöhe) / 2 sein.
        val verticalPadding = (height - itemHeight) / 2
        val safePadding = if (verticalPadding < 0.dp) 0.dp else verticalPadding
        
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = safePadding),
            verticalArrangement = Arrangement.spacedBy((-120).dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(items = tasks, key = { _, t -> t.id }) { index, task ->
                
                val isAnimating = animatingTaskIds.contains(task.id)
                
                val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }
                val itemInfo = layoutInfo.visibleItemsInfo.find { it.index == index }
                
                var scale = 0.85f
                var alpha = 0.6f 
                var isCentered = false
                var rotationX = 0f
                var zIndex = 0f
                
                if (itemInfo != null) {
                    val viewportCenter = (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2f + layoutInfo.viewportStartOffset
                    val itemCenter = itemInfo.offset + itemInfo.size / 2f
                    val distance = (viewportCenter - itemCenter)
                    val visibleHeight = layoutInfo.viewportEndOffset.toFloat()
                    
                    val range = visibleHeight / 2.5f
                    val normalizedDistance = (abs(distance) / range).coerceIn(0f, 1f)
                    
                    scale = 1f - (0.2f * normalizedDistance)
                    alpha = 1f - (0.5f * normalizedDistance)
                    rotationX = (distance / range) * 20f 
                    zIndex = 100f - abs(distance)
                    
                    isCentered = abs(distance) < (itemInfo.size / 2f)
                }

                // Slide Animation nach rechts wegwischen
                val exitTranslationX by animateFloatAsState(targetValue = if (isAnimating) 2000f else 0f, label = "exitTranslation")
                
                val finalScale = scale
                val finalAlpha = alpha

                WheelTaskItem(
                    task = task,
                    scale = finalScale,
                    alpha = finalAlpha,
                    rotationX = rotationX,
                    zIndex = zIndex,
                    isCentered = isCentered,
                    isAnimating = isAnimating,
                    translationX = exitTranslationX,
                    onClick = { onTaskClick(task) }
                )
            }
        }
    }
}

@Composable
fun WheelTaskItem(
    task: Task,
    scale: Float,
    alpha: Float,
    rotationX: Float,
    zIndex: Float,
    isCentered: Boolean,
    isAnimating: Boolean,
    translationX: Float,
    onClick: () -> Unit
) {
    val density = LocalDensity.current
    val categoryColor = when(task.category) {
        Category.WORK -> Color(0xFF6C5CE7)
        Category.PERSONAL -> Color(0xFF00B894)
        Category.PHYSICAL -> Color(0xFFE17055)
        Category.STUDY -> Color(0xFF0984E3)
        Category.OTHER -> Color(0xFFA29BFE)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // Große Höhe beibehalten
            .zIndex(zIndex) // Hier korrekt angewendet
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scale
                this.alpha = alpha
                this.rotationX = rotationX
                this.translationX = translationX
                this.cameraDistance = 12f * density.density
            }
            .padding(vertical = 8.dp, horizontal = 15.dp)
            .background(categoryColor.copy(alpha = 0.85f), RoundedCornerShape(24.dp))
            // RAHMEN HINZUGEFÜGT
            .border(width = 2.dp, color = categoryColor.copy(alpha = 0.3f), shape = RoundedCornerShape(24.dp))
            .then(if (isCentered && !isAnimating) Modifier.clickable { onClick() } else Modifier)
            // Padding angepasst: Weniger links/rechts padding innen, damit Inhalt mehr an den Rand kann
            .padding(20.dp), 
        contentAlignment = Alignment.TopStart 
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp) 
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(40.dp)
                    .background(categoryColor, RoundedCornerShape(3.dp))
            )
            
            Spacer(modifier = Modifier.width(12.dp)) // Etwas weniger Abstand zum Text
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.category.name,
                    // HIER GEÄNDERT: Farbe statt categoryColor nun DarkGray (0xFF1E1E24 - gleiche Farbe wie Titel)
                    color = Color(0xFF1E1E24).copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = task.title,
                    color = Color(0xFF1E1E24),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )
            }
            
            if (isCentered) {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp) // Kleines Padding damit er nicht am Text klebt
                        .size(64.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isAnimating) {
                         Icon(
                             Icons.Rounded.Check, 
                             contentDescription = null, 
                             tint = categoryColor,
                             modifier = Modifier.size(42.dp)
                         )
                    } else {
                         Box(modifier = Modifier
                             .size(40.dp)
                             .background(Color.Transparent)
                             .drawBehind { 
                                 drawCircle(
                                     color = Color.LightGray, 
                                     style = Stroke(width = 3.dp.toPx())
                                 )
                             }
                         )
                    }
                }
            }
        }
    }
}
