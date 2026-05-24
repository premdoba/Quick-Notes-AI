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
fun TermsConditionsScreen(
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
                        text = "Terms & Conditions",
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
                        text = "QuickNotes AI Terms",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text =
                            "QuickNotes AI is designed for educational and productivity purposes only."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text =
                            "Users are responsible for reviewing AI-generated content before relying on it for exams, assignments, or professional use."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text =
                            "You agree not to misuse the application or attempt unauthorized access to services."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text =
                            "Features and content may change or improve over time without prior notice."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text =
                            "Continued use of the app means you accept these terms and conditions."
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