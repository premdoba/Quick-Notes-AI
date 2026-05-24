package com.example.quicknotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quicknotes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    navController: NavController
) {

    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        )
    )

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        text = "Privacy Policy",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_24),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "QuickNotes AI Privacy Policy",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text =
                            "QuickNotes AI values your privacy. " +
                                    "The app may temporarily process text you provide to generate study notes, summaries, and quizzes. " +
                                    "We do not intentionally collect personal sensitive information."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text =
                            "Your generated study materials and quiz history are stored locally on your device for a better user experience."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text =
                            "Internet access is required for AI-powered features such as generating notes and quizzes."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text =
                            "By using QuickNotes AI, you agree to this privacy policy."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Last Updated: May 2026",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}