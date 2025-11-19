package org.example.project.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

import org.example.project.data.commonData.Destination
import org.example.project.data.commonData.Note
import org.example.project.data.commonData.Task
import org.example.project.ui.screens.auth.AuthViewModel
import org.example.project.ui.screens.auth.EnterScreen
import org.example.project.ui.screens.auth.OverviewScreen
import org.example.project.ui.screens.auth.RegistrationScreen
import org.example.project.ui.screens.groupsScreen.detailedGroupScreen.DetailGroupScreen
import org.example.project.ui.screens.groupsScreen.detailedGroupScreen.DetailGroupScreenArgs
import org.example.project.ui.screens.groupsScreen.detailedGroupScreen.DetailedGroupViewModel
import org.example.project.ui.screens.groupsScreen.GroupsScreen
import org.example.project.ui.screens.groupsScreen.conditionScreens.GroupsViewModel
import org.example.project.ui.screens.notesScreen.DetailNoteScreen
import org.example.project.ui.screens.notesScreen.DetailNoteScreenArgs
import org.example.project.ui.screens.notesScreen.NoteCreateDialog
import org.example.project.ui.screens.notesScreen.NotesScreen
import org.example.project.ui.screens.notesScreen.creatingNoteScreens.CheckAnalysisScreen
import org.example.project.ui.screens.notesScreen.creatingNoteScreens.CheckAnalysisScreenArgs
import org.example.project.ui.screens.notesScreen.creatingNoteScreens.CheckAnalysisViewModel
import org.example.project.ui.screens.notesScreen.creatingNoteScreens.CheckTranscribingScreen
import org.example.project.ui.screens.notesScreen.creatingNoteScreens.CheckTranscribingScreenArgs
import org.example.project.ui.screens.notesScreen.creatingNoteScreens.CheckTranscribingViewModel
import org.example.project.ui.screens.notesScreen.creatingNoteScreens.StartAnalysisScreen
import org.example.project.ui.screens.notesScreen.creatingNoteScreens.StartAnalysisScreenArgs
import org.example.project.ui.screens.notesScreen.creatingNoteScreens.StartAnalysisViewModel
import org.example.project.ui.screens.settingsScreen.SettingsScreen
import org.example.project.ui.screens.tasksScreen.DetailTaskScreen
import org.example.project.ui.screens.tasksScreen.DetailTaskScreenArgs
import org.example.project.ui.screens.tasksScreen.TaskCreateDialog
import org.example.project.ui.screens.tasksScreen.TasksScreen
import org.example.project.ui.viewComponents.commonComponents.BottomNavigationBar
import org.example.project.ui.viewmodels.NotesViewModel
import org.example.project.ui.viewmodels.TasksViewModel

import org.jetbrains.compose.resources.StringResource
import taskconvertaiapp.composeapp.generated.resources.Res
import taskconvertaiapp.composeapp.generated.resources.*

enum class TaskConvertAIAppScreens(val title: StringResource) {
    Overview(title = Res.string.overview_screen),
    SignUp(title = Res.string.sign_up_screen),
    SignIn(title = Res.string.sign_in_screen),
    Home(title = Res.string.home_screen)
}

@Composable
fun ChooseCreateDialog(
    currentRoute: String?,
    onDismiss: () -> Unit,
    navController: NavController,
    viewModel: TaskConvertAIViewModel
) {
    when (currentRoute) {
        Destination.NOTES.route -> {
            NoteCreateDialog(
                viewModel = viewModel,
                onDismiss = onDismiss,
                onConfirm = { route ->
                    onDismiss()
                    if (route == "create_manual_note") {
                        navController.navigate(DetailNoteScreenArgs(noteID = null, isEditMode = true))
                    } else {
                        navController.navigate(route)
                    }
                }
            )
        }

        Destination.TASKS.route -> {
            TaskCreateDialog(
                onDismiss = onDismiss,
                onConfirm = { route ->
                    onDismiss()
                    if (route == "create_manual_task") {
                        navController.navigate(DetailTaskScreenArgs(taskID = null, isEditMode = true))
                    } else {
                        navController.navigate(route)
                    }
                },
                navController = navController
            )
        }

        Destination.GROUPS.route -> {
            // Show create group dialog
            onDismiss()
        }

        else -> {
            // Do nothing or show a default dialog
            onDismiss()
        }
    }
}

@Composable
fun TaskConvertAIApp(
    viewModel: TaskConvertAIViewModel = viewModel(factory = TaskConvertAIViewModel.Factory),
    navController: NavHostController = rememberNavController()
) {
//    HideSystemBarsWithInsetsController()
    val viewModelTasks: TasksViewModel = viewModel(factory = TasksViewModel.Factory)
    val  viewModelNotes: NotesViewModel = viewModel(factory = NotesViewModel.Factory)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ChooseCreateDialog(currentRoute, { showDialog = false }, navController, viewModel)
    }

    val shouldShowBottomBar = currentRoute in Destination.entries.map { it.route }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavigationBar(navController)
            }
        },
        floatingActionButton = {
            if (shouldShowBottomBar) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.offset(y = 48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.scale(1.5F)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        NavHost(
            navController = navController,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) },
//            startDestination = if (viewModel.showOverview) TaskConvertAIAppScreens.Overview.name else TaskConvertAIAppScreens.SignIn.name
            startDestination = Destination.NOTES.route
        ) {
            composable(route = TaskConvertAIAppScreens.Overview.name) {
//                BackHandler(true) { }
                OverviewScreen(
                    onCompleteOverviewButtonClicked = {
                        navController.navigate(TaskConvertAIAppScreens.SignUp.name)
                    }
                )
            }
            composable(route = TaskConvertAIAppScreens.SignUp.name) {
//                BackHandler(true) { }
                RegistrationScreen(
                    authViewModel = viewModel(factory = AuthViewModel.Factory),
                    onSuccessSignUp = {
                        navController.navigate(Destination.NOTES.route)
                    },
                    onMoveToSignInClicked = {
                        navController.navigate(TaskConvertAIAppScreens.SignIn.name)
                    }
                )
            }
            composable(route = TaskConvertAIAppScreens.SignIn.name) {
//                BackHandler(true) { }
                EnterScreen(
                    authViewModel = viewModel(factory = AuthViewModel.Factory),
                    onSuccessSignIn = {
                        navController.navigate(Destination.NOTES.route)
                    },
                    onMoveToSignUpClicked = {
                        navController.navigate(TaskConvertAIAppScreens.SignUp.name)
                    }
                )
            }

//            composable(route = TaskConvertAIAppScreens.Home.name) {
////                BackHandler(true) { }
//                HomeScreen()
//            }

            Destination.entries.forEach { destination ->
                composable(destination.route) {
                    when (destination) {
                        Destination.NOTES -> NotesScreen(
                            navController
                        )

                        Destination.TASKS -> TasksScreen(
                            navController,
                            viewModel(factory = org.example.project.ui.screens.tasksScreen.TasksViewModel.Factory)
                        )

                        Destination.GROUPS -> GroupsScreen(
                            navController,
                            viewModel(factory = GroupsViewModel.Factory)
                        )

                        Destination.SETTINGS -> SettingsScreen()
                    }
                }
            }

            composable<CheckTranscribingScreenArgs> { currentBackStackEntry ->
                val args: CheckTranscribingScreenArgs = currentBackStackEntry.toRoute()
                val viewModel: CheckTranscribingViewModel =
                    viewModel(factory = CheckTranscribingViewModel.Factory)
                viewModel.loadJobResult(args.jobId)

                CheckTranscribingScreen(navController = navController, viewModel)
            }

            composable<StartAnalysisScreenArgs> { currentBackStackEntry ->
                val args: StartAnalysisScreenArgs = currentBackStackEntry.toRoute()
                val viewModel: StartAnalysisViewModel =
                    viewModel(factory = StartAnalysisViewModel.Factory)
                viewModel.loadArgs(args.jobId, args.text, args.hints)

                StartAnalysisScreen(navController = navController, viewModel)
            }

            composable<CheckAnalysisScreenArgs> { currentBackStackEntry ->
                val args: CheckAnalysisScreenArgs = currentBackStackEntry.toRoute()
                val viewModel: CheckAnalysisViewModel =
                    viewModel(factory = CheckAnalysisViewModel.Factory)
                viewModel.loadJobResult(args.jobId)

                CheckAnalysisScreen(navController = navController, viewModel = viewModel)
            }

            composable<DetailNoteScreenArgs> { currentBackStackEntry ->
                val detailNoteScreenArgs: DetailNoteScreenArgs = currentBackStackEntry.toRoute()
                val noteID = detailNoteScreenArgs.noteID
                val isEditMode = detailNoteScreenArgs.isEditMode

                var note by remember { mutableStateOf<Note?>(null) }

                // Загружаем данные в корутине, если noteID не null
                LaunchedEffect(noteID) {
                    note = if (noteID != null) {
                        viewModelNotes.getNoteById(noteID.toLong())
                    } else {
                        null
                    }
                }

                DetailNoteScreen(
                    note = note,
                    navController = navController,
                    isEditMode = isEditMode,
                    availableGroups = emptyList(),
                    onSave = { updatedNote ->
                        if (updatedNote.id == 0L || noteID == null) {
                            // Создание новой заметки
                            viewModelNotes.addNote(updatedNote)
                        } else {
                            // Обновление существующей заметки
                            viewModelNotes.updateNote(updatedNote.id, updatedNote)
                        }
                    },
                    onDelete = { noteToDelete ->
                        viewModelNotes.deleteNote(noteToDelete.id)
                    }
                )
            }

            composable<DetailTaskScreenArgs> { currentBackStackEntry ->
                val detailTaskScreenArgs: DetailTaskScreenArgs = currentBackStackEntry.toRoute()
                val taskID = detailTaskScreenArgs.taskID
                val isEditMode = detailTaskScreenArgs.isEditMode

                var task by remember { mutableStateOf<Task?>(null) }

                // Загружаем данные в корутине, если taskID не null
                LaunchedEffect(taskID) {
                    task = if (taskID != null) {
                        viewModelTasks.getTaskById(taskID)
                    } else {
                        null
                    }
                }

                DetailTaskScreen(
                    task = task,
                    navController = navController,
                    isEditMode = isEditMode,
                    availableGroups = emptyList(),
                    availableUsers = emptyList(),
                    onSave = { updatedTask ->
                        if (updatedTask.id.isEmpty() || taskID == null) {
                            // Создание новой задачи
                            viewModelTasks.addTask(updatedTask)
                        } else {
                            // Обновление существующей задачи
                            viewModelTasks.updateTask(updatedTask.id, updatedTask)
                        }
                    },
                    onDelete = { taskToDelete ->
                        viewModelTasks.deleteTask(taskToDelete.id)
                    }
                )
            }

            composable<DetailGroupScreenArgs> { currentBackStackEntry ->
                val detailGroupScreenArgs: DetailGroupScreenArgs = currentBackStackEntry.toRoute()

                val groupName = detailGroupScreenArgs.groupName
                val groupVM: DetailedGroupViewModel = viewModel(factory = DetailedGroupViewModel.Factory)
                groupVM.setGroup(groupName)
                DetailGroupScreen(groupVM, navController)
            }
        }
    }
}

//@Composable
//fun HideSystemBarsWithInsetsController() {
//    val view = LocalView.current
//
//    SideEffect {
//        val insetsController = ViewCompat.getWindowInsetsController(view)
//
//        insetsController?.let {
//            it.hide(WindowInsetsCompat.Type.navigationBars())
//            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
//    }
//}
