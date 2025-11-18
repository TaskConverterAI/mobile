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
import org.example.project.ui.screens.groupsScreen.GroupsScreen
import org.example.project.ui.screens.notesScreen.DetailNoteScreen
import org.example.project.ui.screens.notesScreen.DetailNoteScreenArgs
import org.example.project.ui.screens.notesScreen.NoteCreateDialog
import org.example.project.ui.screens.notesScreen.NotesScreen
import org.example.project.ui.screens.notesScreen.creatingNoteScreens.CheckAnalysisScreen
import org.example.project.ui.screens.notesScreen.creatingNoteScreens.CheckTranscribingScreen
import org.example.project.ui.screens.notesScreen.creatingNoteScreens.StartTranscribingScreen
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
fun ChooseCreateDialog(currentRoute: String?, onDismiss: () -> Unit, navController: NavController) {
    when (currentRoute) {
        Destination.NOTES.route -> {
            NoteCreateDialog(
                onDismiss = onDismiss,
                onConfirm = { route ->
                    onDismiss()
                    navController.navigate(route)
                }
            )
        }
        Destination.TASKS.route -> {

            TaskCreateDialog(
                onDismiss = onDismiss,
                onConfirm = { route ->
                    onDismiss()
                    navController.navigate(route)
                },
                notes = emptyList(),
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
    viewModel: TaskConvertAIViewModel = viewModel(factory = TaskConvertAIViewModel.Companion.Factory),
    navController: NavHostController = rememberNavController()
) {
//    HideSystemBarsWithInsetsController()
    val viewModelTasks: TasksViewModel = viewModel(factory = TasksViewModel.Factory)
    val  viewModelNotes: NotesViewModel = viewModel(factory = NotesViewModel.Factory)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ChooseCreateDialog(currentRoute, { showDialog = false }, navController)
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
                        navController.navigate(TaskConvertAIAppScreens.SignIn.name)
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
                        Destination.NOTES -> NotesScreen(navController)
                        Destination.TASKS -> TasksScreen(navController)
                        Destination.GROUPS -> GroupsScreen()
                        Destination.SETTINGS -> SettingsScreen()
                    }
                }
            }

            composable("start_transcribing_screen") {
                StartTranscribingScreen(navController = navController)
            }

            composable("check_transcribing_screen") {
                CheckTranscribingScreen(navController = navController)
            }

            composable("check_analysis_screen") {
                CheckAnalysisScreen(navController = navController)
            }

            composable<DetailNoteScreenArgs> { currentBackStackEntry ->
                val detailNoteScreenArgs: DetailNoteScreenArgs = currentBackStackEntry.toRoute()
                val noteID = detailNoteScreenArgs.noteID
                var note by remember { mutableStateOf<Note?>(null) }

                // Загружаем данные в корутине
                LaunchedEffect(noteID) {
                    note = viewModelNotes.getNoteById(noteID)
                }

                DetailNoteScreen(note = note, navController = navController)
            }


            composable<DetailTaskScreenArgs> { currentBackStackEntry ->
                val detailTaskScreenArgs: DetailTaskScreenArgs = currentBackStackEntry.toRoute()
                val taskID = detailTaskScreenArgs.taskID;

                var task by remember { mutableStateOf<Task?>(null) }

                // Загружаем данные в корутине
                LaunchedEffect(taskID) {
                    task = viewModelTasks.getTaskById(taskID)
                }

               DetailTaskScreen(task = task, navController)
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
