package com.example.studyflow.domain.model

import com.example.studyflow.data.database.entities.StudyTask

data class DailyPlan(
    val date: Long,
    val tasks: List<PlannedTask>,
    val totalMinutes: Int
)

data class PlannedTask(
    val task: StudyTask,
    val allocatedMinutes: Int,
    val priority: Float
)
