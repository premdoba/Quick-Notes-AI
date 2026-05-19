package com.example.quicknotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.quicknotes.ui.AppNavigation
import com.example.quicknotes.ui.theme.QuickNotesTheme
import com.example.quicknotes.viewmodel.StudyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QuickNotesTheme {
                val navController = rememberNavController()
                val studyViewModel: StudyViewModel = viewModel()

                AppNavigation(navController, studyViewModel)
            }
        }
    }
}