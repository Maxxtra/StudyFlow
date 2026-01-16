package com.example.studyflow.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AllTasks : Screen("all_tasks")
    object Subjects : Screen("subjects")
    object AddTask : Screen("add_task")
    object EditTask : Screen("edit_task/{taskId}") {
        fun createRoute(taskId: Long) = "edit_task/$taskId"
    }
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: Long) = "task_detail/$taskId"
    }
    object Settings : Screen("settings")
    object History : Screen("history")
}
