package com.example.studyflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.studyflow.ui.navigation.NavGraph
import com.example.studyflow.ui.navigation.Screen
import com.example.studyflow.ui.theme.StudyFlowTheme
import com.example.studyflow.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyFlowTheme {
                StudyFlowApp()
            }
        }
    }
}

@Composable
fun StudyFlowApp() {
    val application = androidx.compose.ui.platform.LocalContext.current.applicationContext as StudyFlowApplication

    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            application.taskRepository,
            application.settingsDataStore
        )
    )

    val subjectViewModel: SubjectViewModel = viewModel(
        factory = SubjectViewModelFactory(application.subjectRepository)
    )

    val taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(application.taskRepository, application.settingsDataStore)
    )

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(application.settingsDataStore)
    )

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = { navController.navigate(Screen.Home.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("All Tasks") },
                    selected = false,
                    onClick = { navController.navigate(Screen.AllTasks.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.School, contentDescription = null) },
                    label = { Text("Subjects") },
                    selected = false,
                    onClick = { navController.navigate(Screen.Subjects.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    label = { Text("History") },
                    selected = false,
                    onClick = { navController.navigate(Screen.History.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { navController.navigate(Screen.Settings.route) }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavGraph(
                navController = navController,
                mainViewModel = mainViewModel,
                subjectViewModel = subjectViewModel,
                taskViewModel = taskViewModel,
                settingsViewModel = settingsViewModel
            )
        }
    }
}
