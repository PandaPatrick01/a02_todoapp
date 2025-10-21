package com.example.a02_todoapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
//import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.a02_todoapp.data.local.ToDoViewModel
import com.example.todoapp.categoryColors
import kotlinx.coroutines.launch

/**
 * Screen: bindet UI an das ViewModel (keine eigene Task-Liste hier!)
 * - Tasks kommen aus vm.tasks (Flow/StateFlow)
 * - Filter ist lokaler UI-State
 * - Snackbar/Undo wird hier gehandhabt (optional)
 */
@Composable
fun ToDoScreen(vm: ToDoViewModel) {
    val tasks by vm.tasks.collectAsStateWithLifecycle()
    var filter by rememberSaveable { mutableStateOf(Filter.ALL) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val deletedMsg = stringResource(R.string.deleted_msg)
    val undo = stringResource(R.string.undo)

    val filteredTasks = remember(tasks, filter) {
        when (filter) {
             Filter.ALL -> tasks
            Filter.OPEN -> tasks.filter { !it.done }
            Filter.DONE -> tasks.filter { it.done }
        }
    }

    val onAdd: (String) -> Unit = { _ -> vm.add() }
    val onToggle: (Long) -> Unit = { id -> vm.toggle(id) }
    val onDelete: (Long) -> Unit = { id ->
        val removed = tasks.firstOrNull { it.id == id }
        vm.delete(id)
        if (removed != null) {
            scope.launch {
                val res = snackbarHostState.showSnackbar(
                    message = deletedMsg,
                    actionLabel = undo,
                    withDismissAction = true
                )
                if (res == SnackbarResult.ActionPerformed) {
                    vm.onInputChange(removed.title)
                    vm.onCategoryChange(removed.category)
                    vm.add()
                    if (removed.done) {
                        val newest = vm.tasks.value.firstOrNull {
                            it.title == removed.title && it.category == removed.category
                        }
                        newest?.let { vm.toggle(it.id) }
                    }
                }
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        ToDoContent(
            tasks = filteredTasks,
            input = vm.input,
            onInputChange = vm::onInputChange,
            onAdd = onAdd,
            onToggle = onToggle,
            onDelete = onDelete,
            filter = filter,
            onFilterChange = { f -> filter = f },
            modifier = Modifier.padding(padding),
            selectedCategory = vm.selectedCategory,
            onCategoryChange = vm::onCategoryChange
        )
    }
}

/** Filter-Row (state-los) */
@Composable
private fun FilterRow(
    selected: Filter,
    onSelect: (Filter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        androidx.compose.material3.FilterChip(
            selected = selected == Filter.ALL,
            onClick = { onSelect(Filter.ALL) },
            label = { Text(stringResource(R.string.filter_all)) }
        )
        androidx.compose.material3.FilterChip(
            selected = selected == Filter.OPEN,
            onClick = { onSelect(Filter.OPEN) },
            label = { Text(stringResource(R.string.filter_open)) }
        )
        androidx.compose.material3.FilterChip(
            selected = selected == Filter.DONE,
            onClick = { onSelect(Filter.DONE) },
            label = { Text(stringResource(R.string.filter_done)) }
        )
    }
}

/** Category-Picker (Dropdown), state-los, Auswahl kommt von oben */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPicker(
    selected: Category,
    onSelect: (Category) -> Unit,
    categories: List<Category>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = selected.displayName(),
            onValueChange = {},
            label = { Text(stringResource(R.string.category)) },
            placeholder = { Text(stringResource(R.string.task_placeholder)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .width(120.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { cat ->
                androidx.compose.material3.DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CategorySwatch(cat)
                            Text(cat.displayName())
                        }
                    },
                    onClick = {
                        onSelect(cat)
                        expanded = false
                    }
                )
            }
        }
    }
}

/** Lange Anzeigenamen (für Picker) */
@Composable
fun Category.displayName(): String = when (this) {
    Category.PERSONAL -> stringResource(R.string.cat_personal)
    Category.WORK     -> stringResource(R.string.cat_work)
    Category.PHYSICAL -> stringResource(R.string.cat_physical)
    Category.STUDY    -> stringResource(R.string.cat_study)
    Category.OTHER    -> stringResource(R.string.cat_other)
}

/** Kurzformen (für Chips) */
@Composable
fun Category.shortName(): String = when (this) {
    Category.PERSONAL -> stringResource(R.string.cat_personal_short)
    Category.WORK     -> stringResource(R.string.cat_work_short)
    Category.PHYSICAL -> stringResource(R.string.cat_physical_short)
    Category.STUDY    -> stringResource(R.string.cat_study_short)
    Category.OTHER    -> stringResource(R.string.cat_other_short)
}

/** UI-Root (state-los) */
@Composable
fun ToDoContent(
    tasks: List<Task>,
    input: String,
    onInputChange: (String) -> Unit,
    onAdd: (String) -> Unit,
    onToggle: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    filter: Filter,
    onFilterChange: (Filter) -> Unit,
    modifier: Modifier = Modifier,
    selectedCategory: Category,
    onCategoryChange: (Category) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilterRow(selected = filter, onSelect = onFilterChange)

        // Eingabezeile
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            CategoryPicker(
                selected = selectedCategory,
                onSelect = onCategoryChange,
                categories = Category.values().toList()
            )

            TextField(
                value = input,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text(stringResource(R.string.task_label)) },
                placeholder = { Text(stringResource(R.string.task_placeholder)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { if (input.isNotBlank()) onAdd(input) }
                )
            )

            Button(
                onClick = { onAdd(input) },
                enabled = input.isNotBlank(),
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(stringResource(R.string.add))
            }
        }

        // Empty state
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.empty_state),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            return
        }

        // Liste
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = tasks, key = { it.id }) { task ->
                TaskItem(
                    task = task,
                    onToggle = { onToggle(task.id) },
                    onDelete = { onDelete(task.id) }
                )
            }
        }
    }
}

/** Einzelelement der Liste */
@Composable
fun TaskItem(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            androidx.compose.material3.Checkbox(
                checked = task.done,
                onCheckedChange = { onToggle() }
            )
            CategoryChip(task.category)
            Spacer(Modifier.width(6.dp))
            Text(
                task.title,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.done) TextDecoration.LineThrough else null
            )
        }
        // rotes X (ikonischer Delete-Button)
        IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.delete),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

/** Farbiger Tag/Chip je Kategorie (Kurzform) */
@Composable
private fun CategoryChip(category: Category) {
    val colors = categoryColors(category) // ACHTUNG: Funktion in deinem Projekt anlegen/angepasst importieren
    AssistChip(
        onClick = {},
        enabled = false,
        label = { Text(category.shortName()) },
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = colors.container,
            disabledLabelColor = colors.onContainer
        )
    )
}

/** kleiner Farbpunktswatch für den Picker */
@Composable
private fun CategorySwatch(category: Category) {
    val colors = categoryColors(category)
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(colors.container)
    )
}
