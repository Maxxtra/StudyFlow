package com.example.studyflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.studyflow.data.AppContainer
import com.example.studyflow.ui.screens.*
import com.example.studyflow.ui.viewmodel.*

@Composable
fun NavGraph(
    navController: NavHostController,
    appContainer: AppContainer
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val mainViewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(
                    appContainer.taskRepository,
                    appContainer.settingsDataStore,
                    appContainer.holidayRepository
                )
            )
            val subjectViewModel: SubjectViewModel = viewModel(
                factory = SubjectViewModelFactory(appContainer.subjectRepository)
            )
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
            val mainViewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(
                    appContainer.taskRepository,
                    appContainer.settingsDataStore,
                    appContainer.holidayRepository
                )
            )
            val subjectViewModel: SubjectViewModel = viewModel(
                factory = SubjectViewModelFactory(appContainer.subjectRepository)
            )
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
            val subjectViewModel: SubjectViewModel = viewModel(
                factory = SubjectViewModelFactory(appContainer.subjectRepository)
            )
            SubjectsScreen(
                viewModel = subjectViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddTask.route) {
            val taskViewModel: TaskViewModel = viewModel(
                factory = TaskViewModelFactory(
                    appContainer.taskRepository,
                    appContainer.settingsDataStore
                )
            )
            val subjectViewModel: SubjectViewModel = viewModel(
                factory = SubjectViewModelFactory(appContainer.subjectRepository)
            )
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
            val taskViewModel: TaskViewModel = viewModel(
                factory = TaskViewModelFactory(
                    appContainer.taskRepository,
                    appContainer.settingsDataStore
                )
            )
            val subjectViewModel: SubjectViewModel = viewModel(
                factory = SubjectViewModelFactory(appContainer.subjectRepository)
            )
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
            val mainViewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(
                    appContainer.taskRepository,
                    appContainer.settingsDataStore,
                    appContainer.holidayRepository
                )
            )
            TaskDetailScreen(
                taskId = taskId,
                mainViewModel = mainViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Screen.EditTask.createRoute(taskId)) }
            )
        }

        composable(Screen.Settings.route) {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(appContainer.settingsDataStore)
            )
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.History.route) {
            val mainViewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(
                    appContainer.taskRepository,
                    appContainer.settingsDataStore,
                    appContainer.holidayRepository
                )
            )
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

