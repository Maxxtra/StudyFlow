package com.example.studyflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.studyflow.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studyflow.data.database.entities.TaskType
import com.example.studyflow.ui.components.formatDate
import com.example.studyflow.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Long,
    mainViewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    val uiState by mainViewModel.uiState.collectAsState()
    val task = uiState.allTasks.find { it.id == taskId }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.task_details)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    task?.let {
                        if (!it.isCompleted) {
                            IconButton(onClick = onNavigateToEdit) {
                                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_task))
                            }
                        }
                        IconButton(onClick = {
                            scope.launch {
                                mainViewModel.deleteTask(taskId)
                                onNavigateBack()
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (task == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.task_not_found))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = when (task.type) {
                                    TaskType.HOMEWORK -> Icons.Default.Assignment
                                    TaskType.PROJECT -> Icons.Default.Work
                                    TaskType.EXAM -> Icons.Default.School
                                    TaskType.OTHER -> Icons.Default.Task
                                },
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (task.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        DetailRow(
                            icon = Icons.Default.Category,
                            label = "Type",
                            value = when (task.type) {
                                TaskType.HOMEWORK -> "Homework"
                                TaskType.PROJECT -> "Project"
                                TaskType.EXAM -> "Exam"
                                TaskType.OTHER -> "Other"
                            }
                        )

                        DetailRow(
                            icon = Icons.Default.CalendarToday,
                            label = "Deadline",
                            value = formatDate(task.deadline)
                        )

                        DetailRow(
                            icon = Icons.Default.Timer,
                            label = "Estimated Time",
                            value = "${task.estimatedTimeMinutes} minutes"
                        )

                        DetailRow(
                            icon = Icons.Default.TrendingUp,
                            label = "Difficulty",
                            value = "${task.difficulty}/5"
                        )

                        DetailRow(
                            icon = Icons.Default.Star,
                            label = "Priority",
                            value = String.format("%.2f", task.priority)
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (task.isCompleted) {
                            Color.Green.copy(alpha = 0.1f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = when {
                                    task.isCompleted -> "Completed"
                                    task.inProgress -> "In Progress"
                                    else -> "To Do"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    task.isCompleted -> Color.Green
                                    task.inProgress -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                            if (task.completedAt != null) {
                                Text(
                                    text = "Completed on ${formatDate(task.completedAt)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        if (!task.isCompleted) {
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "In Progress",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Switch(
                                        checked = task.inProgress,
                                        onCheckedChange = {
                                            mainViewModel.toggleTaskInProgress(taskId, it)
                                        }
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Mark as Completed",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Switch(
                                        checked = task.isCompleted,
                                        onCheckedChange = {
                                            mainViewModel.toggleTaskCompletion(taskId, it)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
