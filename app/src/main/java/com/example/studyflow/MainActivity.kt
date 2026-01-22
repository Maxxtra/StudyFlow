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
import androidx.compose.ui.res.stringResource
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
    val appContainer = application.appContainer

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_home)) },
                    selected = false,
                    onClick = { navController.navigate(Screen.Home.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_all_tasks)) },
                    selected = false,
                    onClick = { navController.navigate(Screen.AllTasks.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.School, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_subjects)) },
                    selected = false,
                    onClick = { navController.navigate(Screen.Subjects.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_history)) },
                    selected = false,
                    onClick = { navController.navigate(Screen.History.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_settings)) },
                    selected = false,
                    onClick = { navController.navigate(Screen.Settings.route) }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavGraph(
                navController = navController,
                appContainer = appContainer
            )
        }
    }
}
