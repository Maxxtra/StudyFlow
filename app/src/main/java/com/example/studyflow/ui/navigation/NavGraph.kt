package com.example.studyflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.studyflow.ui.screens.*
import com.example.studyflow.ui.viewmodel.MainViewModel
import com.example.studyflow.ui.viewmodel.SubjectViewModel
import com.example.studyflow.ui.viewmodel.TaskViewModel
import com.example.studyflow.ui.viewmodel.SettingsViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    subjectViewModel: SubjectViewModel,
    taskViewModel: TaskViewModel,
    settingsViewModel: SettingsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = mainViewModel,
                subjectViewModel = subjectViewModel,
                onNavigateToAddTask = { navController.navigate(Screen.AddTask.route) },
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                }
            )
        }

        composable(Screen.AllTasks.route) {
            AllTasksScreen(
                viewModel = mainViewModel,
                subjectViewModel = subjectViewModel,
                onNavigateToAddTask = { navController.navigate(Screen.AddTask.route) },
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                }
            )
        }

        composable(Screen.Subjects.route) {
            SubjectsScreen(
                viewModel = subjectViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddTask.route) {
            AddTaskScreen(
                viewModel = taskViewModel,
                subjectViewModel = subjectViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
            EditTaskScreen(
                taskId = taskId,
                viewModel = taskViewModel,
                subjectViewModel = subjectViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
            TaskDetailScreen(
                taskId = taskId,
                mainViewModel = mainViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Screen.EditTask.createRoute(taskId)) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = mainViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                }
            )
        }
    }
}

