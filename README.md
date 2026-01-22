# StudyFlow

## Overview
StudyFlow is an Android school project built with Kotlin and Jetpack Compose. It is a smart study planner for students that helps organize subjects and academic tasks (homework, projects, exams) and generates an automatic daily plan based on task priority rather than a static to-do list.

This repository follows an incremental development approach. The commit history is intentionally structured to reflect step-by-step implementation (setup -> UI -> data -> domain -> features).

Starting with v1.1.x, the project includes multiple bug fixes and architecture improvements (AppContainer for manual dependency injection, better ViewModel scoping, moving UI logic into ViewModels, improved state persistence with rememberSaveable, and migrating hardcoded UI strings to string resources). It also adds a minimal networking component (Retrofit) that retrieves Romanian public holidays from a REST API and displays a holiday banner on the Home screen, with an optional relaxed-plan mode for that day.

This repository follows an incremental development approach. The commit history is intentionally structured to reflect step-by-step implementation (setup -> UI -> data -> domain -> features -> refinements).

## Current Release
- v1.1.0 (bug fixes + architecture refactor + networking: public holidays)
- v1.0.0 (initial functional release)

## Key Features
- Subjects management
  - Create and manage subjects
  - Subject color selection (RGB picker)
  - Search bar and alphabetical sorting for subjects

- Tasks management
  - Create tasks linked to a subject
  - Task types: homework, project, exam, other
  - Task fields: title, description, deadline, estimated time, difficulty
  - Edit and delete tasks
  - Required field validation and user-friendly error messages
  - Task detail screen

- Task status tracking
  - Mark tasks as completed (with confirmation dialogs)
  - Mark tasks as in progress (with visual indicators)
  - Overdue task visual indicators
  - History screen for completed tasks

- Smart daily planning
  - Home screen displays an automatically generated daily plan
  - Priority algorithm considers deadline urgency, difficulty, and estimated time
  - Plan allocation uses the daily available study time configured in settings

- Settings
  - Daily study time preferences
  - Default difficulty and default estimated time used when creating tasks

## Tech Stack
- Kotlin
- Jetpack Compose (UI)
- Material 3 (design system)
- Navigation Compose (screen navigation)
- ViewModel + StateFlow (reactive UI state)
- Coroutines (asynchronous work)
- Room (local persistence)
- DataStore (user preferences)

## Project Structure
This is a standard Android Studio Gradle project with a single app module.

### Root level
- build.gradle.kts, settings.gradle.kts, gradle.properties
- gradle/ and libs.versions.toml (Version Catalog)
- gradlew / gradlew.bat (Gradle wrapper)

### App module
- app/src/main/java/com/example/studyflow
  - MainActivity.kt (app entry point, sets up Compose + navigation)
  - StudyFlowApplication.kt (initializes database and repositories)

- app/src/main/java/com/example/studyflow/data
  - database/
    - entities/ (Subject, StudyTask, TaskType)
    - dao/ (SubjectDao, StudyTaskDao)
    - StudyFlowDatabase.kt (Room database)
  - repository/ (SubjectRepository, StudyTaskRepository)
  - datastore/ (SettingsDataStore)

- app/src/main/java/com/example/studyflow/domain
  - model/ (DailyPlan, PlannedTask)
  - scheduler/ (TaskScheduler)

- app/src/main/java/com/example/studyflow/ui
  - theme/ (Color, Type, Theme)
  - navigation/ (Screen routes, NavGraph)
  - viewmodel/ (MainViewModel, TaskViewModel, SubjectViewModel, SettingsViewModel, ViewModelFactory)
  - components/ (TaskCard)
  - screens/ (Home, AllTasks, AddTask, EditTask, Subjects, TaskDetail, History, Settings)

## Architecture Summary
StudyFlow uses a simple layered structure:
- Data layer: Room stores subjects and tasks; DataStore stores user settings
- Domain layer: TaskScheduler computes task priorities and generates the daily plan
- UI layer: Compose screens + Navigation + ViewModels expose features and reactive state

## Development History (Commit-Based Roadmap)
The repository commit history is organized as a guide for how the project was built.

### Phase 1: Project setup
1. Initial project setup
2. Add Android app module configuration
3. Add Jetpack Compose and Material3 dependencies
4. Setup Material3 theme and colors

### Phase 2: Persistence (Room)
5. Create database entities for tasks and subjects
6. Add Room database dependencies and KSP configuration
7. Implement Room DAOs for tasks and subjects
8. Setup Room database with version control
9. Implement repository pattern for data abstraction

### Phase 3: Settings (DataStore)
10. Add DataStore for user preferences storage

### Phase 4: Planning domain
11. Create domain models for daily planning
12. Implement task priority algorithm and daily planner

### Phase 5: App wiring
13. Create Application class with repository initialization
14. Add ViewModels with StateFlow for reactive UI
15. Configure Navigation Compose with screen routes

### Phase 6: UI and screens
16. Build reusable TaskCard component with Material3
17. Create HomeScreen with daily plan display
18. Add task creation and editing screens with validation
19. Implement SubjectsScreen with RGB color picker
20. Create task details and history screens
21. Add AllTasksScreen with advanced filtering and sorting
22. Create SettingsScreen with study preferences

### Phase 7: UX improvements and refinements
23. Add search bar and alphabetical sorting to subjects
24. Add task completion confirmation dialogs
25. Implement visual indicators for overdue tasks
26. Add in-progress status for tasks with visual indicators
27. Add required field indicators and validation errors
28. Display real subject names and colors in task cards
29. Load default difficulty and time from settings
30. Add comprehensive documentation and MIT license
