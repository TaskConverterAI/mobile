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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
import org.example.project.data.commonData.Priority
import org.example.project.data.commonData.Status
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
import org.example.project.ui.screens.notesScreen.NotesViewModel
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
import org.example.project.ui.screens.tasksScreen.TasksViewModel
import org.example.project.ui.viewComponents.commonComponents.BottomNavigationBar

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
                    navController.navigate(route)
                }
            )
        }

        Destination.TASKS.route -> {
            val allNotes = listOf(
                Note(
                    title = "Встреча с командой",
                    content = "Обсудить планы на следующую неделю",
                    geotag = "Офис",
                    group = "Работа",
                    comments = emptyList(),
                    color = Color.Green,
                    contentMaxLines = 2
                ),
                Note(
                    title = "Список покупок",
                    content = "Молоко, хлеб, яйца, масло, сыр, колбаса, овощи и фрукты для недели",
                    geotag = "Супермаркет",
                    group = "Личные",
                    comments = emptyList(),
                    color = Color.Cyan,
                    contentMaxLines = 5
                ),
                Note(
                    title = "Идея для проекта",
                    content = "Реализовать новую функцию в приложении с использованием современных подходов",
                    geotag = "Дом",
                    group = "Важные",
                    comments = emptyList(),
                    color = Color.Magenta,
                    contentMaxLines = 3
                ),
                Note(
                    title = "Задача на день",
                    content = "Закончить отчёт",
                    geotag = "Офис",
                    group = "Работа",
                    comments = emptyList(),
                    color = Color.Yellow,
                    contentMaxLines = 1
                ),
                Note(
                    title = "Напоминание",
                    content = "Позвонить врачу и записаться на приём. Не забыть взять медицинскую карту и результаты анализов",
                    geotag = "Поликлиника",
                    group = "Важные",
                    comments = emptyList(),
                    contentMaxLines = 4
                ),
                Note(
                    title = "Заметка",
                    content = "Короткий текст",
                    geotag = "",
                    group = "Личные",
                    comments = emptyList(),
                    color = Color.LightGray,
                    contentMaxLines = 1
                ),
                Note(
                    title = "План путешествия",
                    content = "Забронировать отель, купить билеты на самолёт, составить маршрут по городу, проверить погоду и упаковать чемодан",
                    geotag = "Париж",
                    group = "Личные",
                    comments = emptyList(),
                    color = Color(0xFF8A2BE2),
                    contentMaxLines = 6
                ),
                Note(
                    title = "Рабочие задачи",
                    content = "Просмотреть код коллег и оставить комментарии",
                    geotag = "Офис",
                    group = "Работа",
                    comments = emptyList(),
                    color = Color(0xFFFFA500),
                    contentMaxLines = 2
                ),
                Note(
                    title = "Важное сообщение",
                    content = "Не забыть отправить отчёт начальнику до конца дня",
                    geotag = "Офис",
                    group = "Важные",
                    comments = emptyList(),
                    color = Color.Red,
                    contentMaxLines = 2
                ),
                Note(
                    title = "Личное развитие",
                    content = "Прочитать главу из книги по саморазвитию и сделать заметки",
                    geotag = "Библиотека",
                    group = "Личные",
                    comments = emptyList(),
                    color = Color.Blue,
                    contentMaxLines = 3
                )
            )

            TaskCreateDialog(
                onDismiss = onDismiss,
                onConfirm = { route ->
                    onDismiss()
                    navController.navigate(route)
                },
                notes = allNotes,
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
            startDestination = if (viewModel.showOverview) TaskConvertAIAppScreens.Overview.name else TaskConvertAIAppScreens.SignIn.name
//            startDestination = Destination.NOTES.route
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
                            navController,
                            viewModel(factory = NotesViewModel.Factory)
                        )

                        Destination.TASKS -> TasksScreen(
                            navController,
                            viewModel(factory = TasksViewModel.Factory)
                        )

                        Destination.GROUPS -> GroupsScreen()
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

                val note = Note(
                    title = "Рабочие задачи",
                    content = "Просмотреть код коллег и оставить комментарии",
                    geotag = "Офис",
                    group = "Работа",
                    comments = emptyList(),
                    color = Color(0xFFFFA500),
                    contentMaxLines = 2
                )

                DetailNoteScreen(note, navController)
            }

            composable<DetailTaskScreenArgs> { currentBackStackEntry ->
                val detailTaskScreenArgs: DetailTaskScreenArgs = currentBackStackEntry.toRoute()
                val taskID = detailTaskScreenArgs.taskID

                val task = Task(
                    title = "task 3",
                    description = "empty",
                    comments = emptyList(),
                    group = "standart",
                    assignee = "me",
                    dueDate = 0,
                    geotag = "empty",
                    priority = Priority.LOW,
                    status = Status.DONE
                )

                DetailTaskScreen(task, navController)
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
