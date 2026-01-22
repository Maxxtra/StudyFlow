package com.example.studyflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.studyflow.R
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
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_task)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
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
                label = { Text(stringResource(R.string.title_required)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = formState.title.isBlank(),
                supportingText = if (formState.title.isBlank()) {
                    { Text(stringResource(R.string.required_field)) }
                } else null
            )

            OutlinedTextField(
                value = formState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text(stringResource(R.string.description_optional)) },
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
                        TaskType.HOMEWORK -> stringResource(R.string.task_type_homework)
                        TaskType.PROJECT -> stringResource(R.string.task_type_project)
                        TaskType.EXAM -> stringResource(R.string.task_type_exam)
                        TaskType.OTHER -> stringResource(R.string.task_type_other)
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.type_label)) },
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
                                        TaskType.HOMEWORK -> stringResource(R.string.task_type_homework)
                                        TaskType.PROJECT -> stringResource(R.string.task_type_project)
                                        TaskType.EXAM -> stringResource(R.string.task_type_exam)
                                        TaskType.OTHER -> stringResource(R.string.task_type_other)
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
                    value = subjectsState.allSubjects.find { it.id == formState.subjectId }?.name
                        ?: stringResource(R.string.select_subject),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.subject_required)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubject) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    isError = formState.subjectId == null,
                    supportingText = if (formState.subjectId == null) {
                        { Text(stringResource(R.string.select_subject_required)) }
                    } else null
                )
                ExposedDropdownMenu(
                    expanded = expandedSubject,
                    onDismissRequest = { expandedSubject = false }
                ) {
                    subjectsState.allSubjects.forEach { subject ->
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
                label = { Text(stringResource(R.string.deadline_label)) },
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
                label = { Text(stringResource(R.string.estimated_time_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Column {
                Text(
                    text = stringResource(R.string.difficulty_label, formState.difficulty),
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
                Text(stringResource(R.string.update))
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
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

