package com.example.studyflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studyflow.data.database.entities.TaskType
import com.example.studyflow.ui.viewmodel.SubjectViewModel
import com.example.studyflow.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    taskId: Long,
    viewModel: TaskViewModel,
    subjectViewModel: SubjectViewModel,
    onNavigateBack: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    val subjectsState by subjectViewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = formState.title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text("Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = formState.title.isBlank(),
                supportingText = if (formState.title.isBlank()) {
                    { Text("Required field") }
                } else null
            )

            OutlinedTextField(
                value = formState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            var expandedType by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedType,
                onExpandedChange = { expandedType = it }
            ) {
                OutlinedTextField(
                    value = when (formState.type) {
                        TaskType.HOMEWORK -> "Homework"
                        TaskType.PROJECT -> "Project"
                        TaskType.EXAM -> "Exam"
                        TaskType.OTHER -> "Other"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedType,
                    onDismissRequest = { expandedType = false }
                ) {
                    TaskType.values().forEach { type ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    when (type) {
                                        TaskType.HOMEWORK -> "Homework"
                                        TaskType.PROJECT -> "Project"
                                        TaskType.EXAM -> "Exam"
                                        TaskType.OTHER -> "Other"
                                    }
                                )
                            },
                            onClick = {
                                viewModel.updateType(type)
                                expandedType = false
                            }
                        )
                    }
                }
            }

            var expandedSubject by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedSubject,
                onExpandedChange = { expandedSubject = it }
            ) {
                OutlinedTextField(
                    value = subjectsState.subjects.find { it.id == formState.subjectId }?.name
                        ?: "Select Subject",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Subject *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubject) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    isError = formState.subjectId == null,
                    supportingText = if (formState.subjectId == null) {
                        { Text("Required field - please select a subject") }
                    } else null
                )
                ExposedDropdownMenu(
                    expanded = expandedSubject,
                    onDismissRequest = { expandedSubject = false }
                ) {
                    subjectsState.subjects.forEach { subject ->
                        DropdownMenuItem(
                            text = { Text(subject.name) },
                            onClick = {
                                viewModel.updateSubjectId(subject.id)
                                expandedSubject = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(formState.deadline)),
                onValueChange = {},
                readOnly = true,
                label = { Text("Deadline") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.estimatedTimeMinutes.toString(),
                onValueChange = { value ->
                    value.toIntOrNull()?.let { viewModel.updateEstimatedTime(it) }
                },
                label = { Text("Estimated Time (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Column {
                Text(
                    text = "Difficulty: ${formState.difficulty}/5",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = formState.difficulty.toFloat(),
                    onValueChange = { viewModel.updateDifficulty(it.toInt()) },
                    valueRange = 1f..5f,
                    steps = 3
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.updateTask(taskId, onSuccess = onNavigateBack)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = formState.title.isNotBlank() && formState.subjectId != null
            ) {
                Text("Update")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = formState.deadline
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.updateDeadline(it)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
