StudyFlow is an Android school project built with Kotlin and Jetpack Compose. It is a smart study planner that helps students manage subjects and study tasks homework projects exams and generates an automatic daily plan based on priority instead of using a simple static to do list.

What the app does
The user can create subjects and then add study tasks linked to a subject. Each task has a title description type deadline estimated time and difficulty. The app shows active tasks upcoming deadlines and also keeps a history of completed tasks. The Home screen highlights the daily plan generated automatically using a scheduling algorithm that considers deadline difficulty and estimated time. The user can also mark tasks as completed set a task as in progress edit tasks and delete tasks. Settings allow configuring daily study hours and default values used when creating new tasks.

Project structure overview based on the zip
The project is a standard Android Studio Gradle project with a single app module.

Root level
build.gradle.kts and settings.gradle.kts hold the global Gradle configuration
gradle folder and libs.versions.toml handle dependency versions using Version Catalog

app module
app/src/main contains all application code and resources

Core packages and responsibilities

com.example.studyflow
MainActivity.kt
Entry point of the app. Sets up the Compose UI theme and creates the Scaffold with the bottom navigation bar. It also creates the NavController and wires navigation between screens.

StudyFlowApplication.kt
Application class used as a simple dependency container. It initializes the Room database and exposes repositories plus the SettingsDataStore so ViewModels can be created with factories.

data layer
com.example.studyflow.data.database
StudyFlowDatabase.kt
Room database configuration. Contains Subject and StudyTask as entities and provides DAOs. Uses fallbackToDestructiveMigration for development simplicity.

Entities in data.database.entities
Subject
Represents a course subject name and color.
StudyTask
Represents a study item homework project exam or other. Includes deadline estimatedTimeMinutes difficulty completion state inProgress completedAt createdAt and a priority field stored in DB.
TaskType
Enum for HOMEWORK PROJECT EXAM OTHER

DAOs in data.database.dao
SubjectDao
CRUD operations for subjects.
StudyTaskDao
CRUD operations for tasks plus queries for all tasks active tasks completed tasks tasks by subject tasks by type tasks between dates and update helpers for completion inProgress and priority.

Converters.kt
Room type converters used for persisting non primitive types such as enums.

com.example.studyflow.data.datastore
SettingsDataStore.kt
DataStore Preferences used for app settings. Stores daily study hours default difficulty and default estimated time. Exposes them as Flow and provides suspend setters.

com.example.studyflow.data.repository
SubjectRepository.kt and StudyTaskRepository.kt
Repositories wrap the DAOs and expose Flow streams to the UI layer. They also provide suspend functions for insert update delete and specific updates like completion inProgress and priority.

domain layer
com.example.studyflow.domain.scheduler
TaskScheduler.kt
This is the core logic that makes the app “smart”. It computes a priority score for each active task using a mix of urgency days until deadline difficulty and estimated time. It then generates a DailyPlan by sorting tasks by priority and allocating available minutes for the day until the daily time budget is used. It also includes a weekly plan helper that generates plans for 7 days.

com.example.studyflow.domain.model
DailyPlan and PlannedTask
Data models used by the scheduler. DailyPlan contains the date a list of planned tasks and the total allocated minutes. PlannedTask links a StudyTask with allocatedMinutes and a computed priority.

ui layer
com.example.studyflow.ui.navigation
Screen.kt defines all app routes
NavGraph.kt defines the Navigation Compose graph and maps routes to screens including routes with taskId for details and edit

com.example.studyflow.ui.viewmodel
MainViewModel
Collects all tasks from the repository and dailyStudyHours from settings then computes the daily plan using TaskScheduler. It also updates task priority in the database and exposes MainUiState to the UI. Handles actions like toggle completion toggle in progress and delete.
TaskViewModel
Holds the state of the add edit task form. Loads default difficulty and estimated time from SettingsDataStore. Provides functions to update form fields and save or update tasks.
SubjectViewModel
Manages the subject list and subject CRUD operations.
SettingsViewModel
Reads and writes settings through DataStore and exposes SettingsUiState.
ViewModelFactory.kt
Factories used to create ViewModels with repositories and DataStore injected from StudyFlowApplication.

com.example.studyflow.ui.screens
HomeScreen
Shows the automatically generated daily plan and quick access to important tasks.
AllTasksScreen
Shows the full organized task list with navigation to details and add.
TaskDetailScreen
Shows full information about one task and actions like complete delete edit.
AddTaskScreen and EditTaskScreen
Forms for creating and editing tasks using TaskViewModel.
SubjectsScreen
Subject management add delete update subjects.
HistoryScreen
Shows completed tasks history.
SettingsScreen
Shows and edits daily study hours and default values.

com.example.studyflow.ui.components
TaskCard.kt
Reusable Compose component to render a task with its main properties and actions in a consistent UI style.

com.example.studyflow.ui.theme
Theme files for Material 3 styling Color Type and Theme.

In short the core of the project is the layered architecture plus the scheduling engine
Data layer Room and DataStore store tasks subjects and settings
Domain layer TaskScheduler calculates priority and builds the daily plan
UI layer Compose screens ViewModels and Navigation show the data and expose actions to the user
