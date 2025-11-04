package com.example.taskconvertaiapp.shared.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.taskconvertaiapp.R
import com.example.taskconvertaiapp.shared.ui.screens.auth.AuthViewModel
import com.example.taskconvertaiapp.shared.ui.screens.auth.EnterScreen
import com.example.taskconvertaiapp.shared.ui.screens.auth.OverviewScreen
import com.example.taskconvertaiapp.shared.ui.screens.auth.RegistrationScreen
import com.example.taskconvertaiapp.shared.ui.screens.tasks.HomeScreen

enum class TaskConvertAIAppScreens(@StringRes val title: Int) {
    Overview(title = R.string.overview_screen),
    SignUp(title = R.string.sign_up_screen),
    SignIn(title = R.string.sign_in_screen),
    Home(title = R.string.home_screen)
}

@Composable
fun TaskConvertAIApp(
    viewModel: TaskConvertAIViewModel = viewModel(factory = TaskConvertAIViewModel.Factory),
    navController: NavHostController = rememberNavController()
) {
    HideSystemBarsWithInsetsController()

    Surface {
        NavHost(
            navController = navController,
            startDestination = if (viewModel.showOverview) TaskConvertAIAppScreens.Overview.name else TaskConvertAIAppScreens.SignIn.name
        ) {
            composable(route = TaskConvertAIAppScreens.Overview.name) {
                BackHandler(true) { }
                OverviewScreen(
                    onCompleteOverviewButtonClicked = {
                        navController.navigate(TaskConvertAIAppScreens.SignUp.name)
                    }
                )
            }
            composable(route = TaskConvertAIAppScreens.SignUp.name) {
                BackHandler(true) { }
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
                BackHandler(true) { }
                EnterScreen(
                    authViewModel = viewModel(factory = AuthViewModel.Factory),
                    onSuccessSignIn = {
                        navController.navigate(TaskConvertAIAppScreens.Home.name)
                    },
                    onMoveToSignUpClicked = {
                        navController.navigate(TaskConvertAIAppScreens.SignUp.name)
                    }
                )
            }

            composable(route = TaskConvertAIAppScreens.Home.name) {
                BackHandler(true) { }
                HomeScreen()
            }
        }
    }
}

@Composable
fun HideSystemBarsWithInsetsController() {
    val view = LocalView.current

    SideEffect {
        val insetsController = ViewCompat.getWindowInsetsController(view)

        insetsController?.let {
            it.hide(WindowInsetsCompat.Type.navigationBars())
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
