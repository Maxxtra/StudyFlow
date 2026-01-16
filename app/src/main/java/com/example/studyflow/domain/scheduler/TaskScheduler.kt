package com.example.studyflow.domain.scheduler

import com.example.studyflow.data.database.entities.StudyTask
import com.example.studyflow.domain.model.DailyPlan
import com.example.studyflow.domain.model.PlannedTask
import java.util.Calendar
import kotlin.math.max
import kotlin.math.min

class TaskScheduler {
    fun calculatePriority(task: StudyTask, currentTime: Long = System.currentTimeMillis()): Float {
        val daysUntilDeadline = ((task.deadline - currentTime) / (1000 * 60 * 60 * 24)).toFloat()
        val urgencyFactor = when {
            daysUntilDeadline <= 0 -> 100f
            daysUntilDeadline <= 1 -> 50f
            daysUntilDeadline <= 3 -> 30f
            daysUntilDeadline <= 7 -> 15f
            else -> max(1f, 100f / daysUntilDeadline)
        }

        val difficultyFactor = task.difficulty * 5f

        val timeFactor = (task.estimatedTimeMinutes / 60f) * 2f

        return urgencyFactor + difficultyFactor + timeFactor
    }

    fun generateDailyPlan(
        tasks: List<StudyTask>,
        dailyAvailableMinutes: Int,
        date: Long = System.currentTimeMillis()
    ): DailyPlan {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        val activeTasks = tasks.filter { !it.isCompleted && it.deadline >= startOfDay }

        val tasksWithPriority = activeTasks.map { task ->
            PlannedTask(
                task = task,
                allocatedMinutes = 0,
                priority = calculatePriority(task, date)
            )
        }.sortedByDescending { it.priority }

        val plannedTasks = mutableListOf<PlannedTask>()
        var remainingMinutes = dailyAvailableMinutes

        for (taskPlan in tasksWithPriority) {
            if (remainingMinutes <= 0) break

            val allocatedTime = min(taskPlan.task.estimatedTimeMinutes, remainingMinutes)

            if (allocatedTime > 0) {
                plannedTasks.add(
                    taskPlan.copy(allocatedMinutes = allocatedTime)
                )
                remainingMinutes -= allocatedTime
            }
        }

        return DailyPlan(
            date = startOfDay,
            tasks = plannedTasks,
            totalMinutes = dailyAvailableMinutes - remainingMinutes
        )
    }

    fun generateWeeklyPlan(
        tasks: List<StudyTask>,
        dailyAvailableMinutes: Int,
        startDate: Long = System.currentTimeMillis()
    ): List<DailyPlan> {
        val weeklyPlans = mutableListOf<DailyPlan>()
        val calendar = Calendar.getInstance()

        for (day in 0 until 7) {
            calendar.timeInMillis = startDate
            calendar.add(Calendar.DAY_OF_MONTH, day)

            val dailyPlan = generateDailyPlan(tasks, dailyAvailableMinutes, calendar.timeInMillis)
            weeklyPlans.add(dailyPlan)
        }

        return weeklyPlans
    }
}
