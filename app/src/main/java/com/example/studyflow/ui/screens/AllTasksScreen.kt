package com.example.studyflow.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studyflow.data.database.entities.TaskType
import com.example.studyflow.ui.components.TaskCard
import com.example.studyflow.ui.viewmodel.MainViewModel
import com.example.studyflow.ui.viewmodel.SubjectViewModel

enum class TaskFilter {
    ALL, ACTIVE, IN_PROGRESS, COMPLETED, OVERDUE, UPCOMING
}

enum class TaskSort {
    DEADLINE_ASC, DEADLINE_DESC, PRIORITY, DIFFICULTY, CREATED_DATE, TYPE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTasksScreen(
    viewModel: MainViewModel,
    subjectViewModel: SubjectViewModel,
    onNavigateToAddTask: () -> Unit,
    onNavigateToTaskDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val subjectsState by subjectViewModel.uiState.collectAsState()

    var selectedFilter by remember { mutableStateOf(TaskFilter.ALL) }
    var selectedSort by remember { mutableStateOf(TaskSort.DEADLINE_ASC) }
    var selectedType by remember { mutableStateOf<TaskType?>(null) }
    var selectedSubjectId by remember { mutableStateOf<Long?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showTypeMenu by remember { mutableStateOf(false) }
    var showSubjectMenu by remember { mutableStateOf(false) }

    val currentTime = System.currentTimeMillis()

    val filteredTasks = uiState.allTasks
        .filter { task ->
            val matchesFilter = when (selectedFilter) {
                TaskFilter.ALL -> true
                TaskFilter.ACTIVE -> !task.isCompleted && !task.inProgress
                TaskFilter.IN_PROGRESS -> task.inProgress
                TaskFilter.COMPLETED -> task.isCompleted
                TaskFilter.OVERDUE -> !task.isCompleted && task.deadline < currentTime
                TaskFilter.UPCOMING -> !task.isCompleted && task.deadline >= currentTime
            }

            val matchesType = selectedType == null || task.type == selectedType
            val matchesSubject = selectedSubjectId == null || task.subjectId == selectedSubjectId

            matchesFilter && matchesType && matchesSubject
        }

    val sortedTasks = when (selectedSort) {
        TaskSort.DEADLINE_ASC -> filteredTasks.sortedBy { it.deadline }
        TaskSort.DEADLINE_DESC -> filteredTasks.sortedByDescending { it.deadline }
        TaskSort.PRIORITY -> filteredTasks.sortedByDescending { it.priority }
        TaskSort.DIFFICULTY -> filteredTasks.sortedByDescending { it.difficulty }
        TaskSort.CREATED_DATE -> filteredTasks.sortedByDescending { it.createdAt }
        TaskSort.TYPE -> filteredTasks.sortedBy { it.type.ordinal }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Tasks") },
                actions = {
                    // Sort button
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Deadline ↑") },
                            onClick = {
                                selectedSort = TaskSort.DEADLINE_ASC
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (selectedSort == TaskSort.DEADLINE_ASC) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Deadline ↓") },
                            onClick = {
                                selectedSort = TaskSort.DEADLINE_DESC
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (selectedSort == TaskSort.DEADLINE_DESC) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Priority") },
                            onClick = {
                                selectedSort = TaskSort.PRIORITY
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (selectedSort == TaskSort.PRIORITY) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Difficulty") },
                            onClick = {
                                selectedSort = TaskSort.DIFFICULTY
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (selectedSort == TaskSort.DIFFICULTY) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Created Date") },
                            onClick = {
                                selectedSort = TaskSort.CREATED_DATE
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (selectedSort == TaskSort.CREATED_DATE) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Type") },
                            onClick = {
                                selectedSort = TaskSort.TYPE
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (selectedSort == TaskSort.TYPE) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddTask) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Statistics
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatItem(
                                label = "Total",
                                value = uiState.allTasks.size.toString(),
                                icon = Icons.Default.Assignment
                            )
                            StatItem(
                                label = "Active",
                                value = uiState.allTasks.count { !it.isCompleted && !it.inProgress }.toString(),
                                icon = Icons.Default.Circle
                            )
                            StatItem(
                                label = "In Progress",
                                value = uiState.allTasks.count { it.inProgress }.toString(),
                                icon = Icons.Default.PlayArrow
                            )
                            StatItem(
                                label = "Overdue",
                                value = uiState.allTasks.count { !it.isCompleted && it.deadline < currentTime }.toString(),
                                icon = Icons.Default.Warning
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Status Filters
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedFilter == TaskFilter.ALL,
                            onClick = { selectedFilter = TaskFilter.ALL },
                            label = { Text("All") }
                        )
                        FilterChip(
                            selected = selectedFilter == TaskFilter.ACTIVE,
                            onClick = { selectedFilter = TaskFilter.ACTIVE },
                            label = { Text("Active") },
                            leadingIcon = {
                                Icon(Icons.Default.Circle, null, modifier = Modifier.size(16.dp))
                            }
                        )
                        FilterChip(
                            selected = selectedFilter == TaskFilter.IN_PROGRESS,
                            onClick = { selectedFilter = TaskFilter.IN_PROGRESS },
                            label = { Text("In Progress") },
                            leadingIcon = {
                                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                            }
                        )
                        FilterChip(
                            selected = selectedFilter == TaskFilter.COMPLETED,
                            onClick = { selectedFilter = TaskFilter.COMPLETED },
                            label = { Text("Completed") },
                            leadingIcon = {
                                Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                            }
                        )
                        FilterChip(
                            selected = selectedFilter == TaskFilter.OVERDUE,
                            onClick = { selectedFilter = TaskFilter.OVERDUE },
                            label = { Text("Overdue") },
                            leadingIcon = {
                                Icon(Icons.Default.Warning, null, modifier = Modifier.size(16.dp))
                            }
                        )
                        FilterChip(
                            selected = selectedFilter == TaskFilter.UPCOMING,
                            onClick = { selectedFilter = TaskFilter.UPCOMING },
                            label = { Text("Upcoming") },
                            leadingIcon = {
                                Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(16.dp))
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Type and Subject Filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Type Filter
                        Box(modifier = Modifier.weight(1f)) {
                            FilterChip(
                                selected = selectedType != null,
                                onClick = { showTypeMenu = true },
                                label = {
                                    Text(
                                        when (selectedType) {
                                            TaskType.HOMEWORK -> "Homework"
                                            TaskType.PROJECT -> "Project"
                                            TaskType.EXAM -> "Exam"
                                            TaskType.OTHER -> "Other"
                                            null -> "All Types"
                                        }
                                    )
                                },
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(18.dp))
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(
                                expanded = showTypeMenu,
                                onDismissRequest = { showTypeMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("All Types") },
                                    onClick = {
                                        selectedType = null
                                        showTypeMenu = false
                                    },
                                    leadingIcon = {
                                        if (selectedType == null) {
                                            Icon(Icons.Default.Check, contentDescription = null)
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Homework") },
                                    onClick = {
                                        selectedType = TaskType.HOMEWORK
                                        showTypeMenu = false
                                    },
                                    leadingIcon = {
                                        if (selectedType == TaskType.HOMEWORK) {
                                            Icon(Icons.Default.Check, contentDescription = null)
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Project") },
                                    onClick = {
                                        selectedType = TaskType.PROJECT
                                        showTypeMenu = false
                                    },
                                    leadingIcon = {
                                        if (selectedType == TaskType.PROJECT) {
                                            Icon(Icons.Default.Check, contentDescription = null)
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Exam") },
                                    onClick = {
                                        selectedType = TaskType.EXAM
                                        showTypeMenu = false
                                    },
                                    leadingIcon = {
                                        if (selectedType == TaskType.EXAM) {
                                            Icon(Icons.Default.Check, contentDescription = null)
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Other") },
                                    onClick = {
                                        selectedType = TaskType.OTHER
                                        showTypeMenu = false
                                    },
                                    leadingIcon = {
                                        if (selectedType == TaskType.OTHER) {
                                            Icon(Icons.Default.Check, contentDescription = null)
                                        }
                                    }
                                )
                            }
                        }

                        // Subject Filter
                        Box(modifier = Modifier.weight(1f)) {
                            FilterChip(
                                selected = selectedSubjectId != null,
                                onClick = { showSubjectMenu = true },
                                label = {
                                    Text(
                                        subjectsState.subjects.find { it.id == selectedSubjectId }?.name
                                            ?: "All Subjects"
                                    )
                                },
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(18.dp))
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(
                                expanded = showSubjectMenu,
                                onDismissRequest = { showSubjectMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("All Subjects") },
                                    onClick = {
                                        selectedSubjectId = null
                                        showSubjectMenu = false
                                    },
                                    leadingIcon = {
                                        if (selectedSubjectId == null) {
                                            Icon(Icons.Default.Check, contentDescription = null)
                                        }
                                    }
                                )
                                subjectsState.subjects.forEach { subject ->
                                    DropdownMenuItem(
                                        text = { Text(subject.name) },
                                        onClick = {
                                            selectedSubjectId = subject.id
                                            showSubjectMenu = false
                                        },
                                        leadingIcon = {
                                            if (selectedSubjectId == subject.id) {
                                                Icon(Icons.Default.Check, contentDescription = null)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    // Results count
                    Text(
                        text = "${sortedTasks.size} task${if (sortedTasks.size != 1) "s" else ""} found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (sortedTasks.isEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No tasks found",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Try adjusting your filters",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(sortedTasks) { task ->
                        val subject = subjectsState.subjects.find { it.id == task.subjectId }
                        TaskCard(
                            task = task,
                            subjectColor = subject?.let { Color(it.color) } ?: Color.Gray,
                            subjectName = subject?.name ?: "Unknown",
                            onTaskClick = { onNavigateToTaskDetail(task.id) },
                            onToggleComplete = {
                                viewModel.toggleTaskCompletion(
                                    task.id,
                                    !task.isCompleted
                                )
                            },
                            onToggleInProgress = { inProgress ->
                                viewModel.toggleTaskInProgress(
                                    task.id,
                                    inProgress
                                )
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

