package org.example.project.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import org.example.project.ui.screens.auth.AuthViewModel
import org.example.project.ui.screens.auth.EnterScreen
import org.example.project.ui.screens.auth.OverviewScreen
import org.example.project.ui.screens.auth.RegistrationScreen
import org.example.project.ui.screens.tasks.HomeScreen

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
fun TaskConvertAIApp(
    viewModel: TaskConvertAIViewModel = viewModel(factory = TaskConvertAIViewModel.Companion.Factory),
    navController: NavHostController = rememberNavController()
) {
//    HideSystemBarsWithInsetsController()

    Surface {
        NavHost(
            navController = navController,
            startDestination = if (viewModel.showOverview) TaskConvertAIAppScreens.Overview.name else TaskConvertAIAppScreens.SignIn.name
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
                        navController.navigate(TaskConvertAIAppScreens.Home.name)
                    },
                    onMoveToSignUpClicked = {
                        navController.navigate(TaskConvertAIAppScreens.SignUp.name)
                    }
                )
            }

            composable(route = TaskConvertAIAppScreens.Home.name) {
//                BackHandler(true) { }
                HomeScreen()
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
