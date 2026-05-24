package com.example.quicknotes.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.quicknotes.ui.screens.AiDisclaimerScreen
import com.example.quicknotes.ui.screens.ContactUsScreen
import com.example.quicknotes.ui.screens.DownloadsScreen
import com.example.quicknotes.viewmodel.StudyViewModel
import com.example.quicknotes.ui.screens.GenerateScreen
import com.example.quicknotes.ui.screens.HistoryDetailScreen
import com.example.quicknotes.ui.screens.HistoryScreen
import com.example.quicknotes.ui.screens.McqDetailScreen
import com.example.quicknotes.ui.screens.McqsScreen
import com.example.quicknotes.ui.screens.PrivacyPolicyScreen
import com.example.quicknotes.ui.screens.QuizScreen
import com.example.quicknotes.ui.screens.SettingsScreen
import com.example.quicknotes.ui.screens.TermsConditionsScreen

sealed class Routes(val route: String) {
    object Generate : Routes("generate")
    object Downloads : Routes("downloads")
    object History : Routes("history")
    object Mcqs : Routes("mcqs")
    object Privacy : Routes("privacy")
    object Contact : Routes("contact")
    object Terms : Routes("terms")
    object AiDisclaimer : Routes("disclaimer")
    object McqDetail: Routes("mcq_detail/{id}") {
        fun createRoute(id: Int) = "mcq_detail/$id"
    }
    object HistoryDetail : Routes("historyDetail/{id}") {
        fun createRoute(id: Int) = "historyDetail/$id"
    }
    object Quiz : Routes("quiz")
    object Settings : Routes("settings")
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

        composable(Routes.Downloads.route) {
            DownloadsScreen(navController)
        }

        composable(Routes.Mcqs.route) {
            McqsScreen(navController, vm)
        }

        composable(
            Routes.McqDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            McqDetailScreen(navController, vm, id)
        }

        composable(Routes.History.route) {
            HistoryScreen(navController, vm)
        }

        composable(Routes.Contact.route) {
            ContactUsScreen(navController)
        }
        composable(Routes.Settings.route) {
            SettingsScreen(navController, vm)
        }

        composable(Routes.Privacy.route) {
            PrivacyPolicyScreen(navController)
        }

        composable(Routes.AiDisclaimer.route) {
            AiDisclaimerScreen(navController)
        }

        composable(Routes.Terms.route) {
            TermsConditionsScreen(navController)
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