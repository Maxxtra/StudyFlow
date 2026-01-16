package com.example.studyflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studyflow.data.database.entities.StudyTask
import com.example.studyflow.data.database.entities.TaskType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskCard(
    task: StudyTask,
    subjectColor: Color,
    subjectName: String,
    onTaskClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onToggleInProgress: ((Boolean) -> Unit)? = null,
    showCheckbox: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showConfirmDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var showSuccessDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    val isOverdue = task.deadline < System.currentTimeMillis() && !task.isCompleted
    val overdueColor = Color(0xFFFF5252)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTaskClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isOverdue) {
            androidx.compose.foundation.BorderStroke(2.dp, overdueColor)
        } else null,
        colors = CardDefaults.cardColors(
            containerColor = when {
                isOverdue -> overdueColor.copy(alpha = 0.1f)
                task.isCompleted -> MaterialTheme.colorScheme.surfaceVariant
                task.inProgress -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .background(subjectColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = when (task.type) {
                            TaskType.HOMEWORK -> Icons.AutoMirrored.Filled.Assignment
                            TaskType.PROJECT -> Icons.Default.Work
                            TaskType.EXAM -> Icons.Default.School
                            TaskType.OTHER -> Icons.Default.Task
                        },
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (isOverdue) {
                        AssistChip(
                            onClick = {},
                            label = { Text("OVERDUE", style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = overdueColor,
                                labelColor = Color.White,
                                leadingIconContentColor = Color.White
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                    } else if (task.inProgress && !task.isCompleted) {
                        AssistChip(
                            onClick = {},
                            label = { Text("In Progress", style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.onPrimary,
                                leadingIconContentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subjectName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isOverdue) overdueColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatDate(task.deadline),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isOverdue) overdueColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${task.estimatedTimeMinutes} min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DifficultyIndicator(difficulty = task.difficulty)
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (showCheckbox) {
                    if (onToggleInProgress != null && !task.isCompleted) {
                        IconButton(
                            onClick = { onToggleInProgress(!task.inProgress) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (task.inProgress) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (task.inProgress) "Pause" else "Start",
                                tint = if (task.inProgress) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = {
                            if (!task.isCompleted) {
                                showConfirmDialog = true
                            } else {
                                onToggleComplete()
                            }
                        }
                    )
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.Green,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Complete Task?") },
            text = { Text("Are you sure you want to mark \"${task.title}\" as completed?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        onToggleComplete()
                        showSuccessDialog = true
                    }
                ) {
                    Text("Yes, Complete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.Green,
                    modifier = Modifier.size(64.dp)
                )
            },
            title = { Text("Task Completed!") },
            text = { Text("Great job! You've successfully completed \"${task.title}\".") },
            confirmButton = {
                Button(onClick = { showSuccessDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun DifficultyIndicator(difficulty: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (index < difficulty) {
                            when {
                                difficulty <= 2 -> Color.Green
                                difficulty <= 3 -> Color.Yellow
                                else -> Color.Red
                            }
                        } else {
                            Color.Gray.copy(alpha = 0.3f)
                        },
                        shape = MaterialTheme.shapes.small
                    )
            )
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
