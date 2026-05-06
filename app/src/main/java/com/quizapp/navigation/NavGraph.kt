package com.quizapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.quizapp.data.QuizRepositoryImpl
import com.quizapp.presentation.*

/**
 * Navigation routes
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Quiz : Screen("quiz")
    object Result : Screen("result")
}

/**
 * Main navigation graph
 */
@Composable
fun QuizNavGraph(
    navController: NavHostController,
    viewModel: QuizViewModel = viewModel(
        factory = QuizViewModelFactory(QuizRepositoryImpl())
    ),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onStartQuiz = {
                    navController.navigate(Screen.Quiz.route)
                }
            )
        }
        
        composable(Screen.Quiz.route) {
            when (val state = uiState) {
                is QuizUiState.Loading -> {
                    LoadingScreen()
                }
                is QuizUiState.Quiz -> {
                    QuizScreen(
                        state = state,
                        onAnswerSelected = viewModel::selectAnswer
                    )
                }
                is QuizUiState.Result -> {
                    // Navigate to result screen
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            }
        }

        composable(Screen.Result.route) {
            when (val state = uiState) {
                is QuizUiState.Result -> {
                    ResultScreen(
                        state = state,
                        onRetry = {
                            viewModel.retryQuiz()
                            navController.navigate(Screen.Quiz.route) {
                                popUpTo(Screen.Result.route) { inclusive = true }
                            }
                        },
                        onBackToHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                }
                else -> {
                    // Fallback - shouldn't happen
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
