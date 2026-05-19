package com.example.quicknotes.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.quicknotes.viewmodel.StudyViewModel
import com.example.quicknotes.ui.screens.GenerateScreen
import com.example.quicknotes.ui.screens.HistoryDetailScreen
import com.example.quicknotes.ui.screens.HistoryScreen
import com.example.quicknotes.ui.screens.QuizScreen

sealed class Routes(val route: String) {
    object Generate : Routes("generate")
    object History : Routes("history")
    object HistoryDetail : Routes("historyDetail/{id}") {
        fun createRoute(id: Int) = "historyDetail/$id"
    }
    object Quiz : Routes("quiz")
}

@Composable
fun AppNavigation(navController: NavHostController, vm: StudyViewModel) {
    NavHost(navController = navController, startDestination = Routes.Generate.route) {

        composable(Routes.Generate.route) {
            GenerateScreen(navController, vm)
        }

        composable(Routes.Quiz.route) {
            QuizScreen(navController, vm)
        }

        composable(Routes.History.route) {
            HistoryScreen(navController, vm)
        }

        composable(
            route = Routes.HistoryDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            HistoryDetailScreen(navController, vm, id)
        }
    }
}