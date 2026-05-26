package com.example.quicknotes.ui.screens

import android.R.attr.text
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
fun AiDisclaimerScreen(
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
                        text = "AI Disclaimer",
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
                        text = "AI Generated Content Disclaimer",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text =
                            "QuickNotes AI uses artificial intelligence to generate educational content such as notes, summaries, important questions, explanations, and MCQs based on user prompts.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // SECTION 1

                    Text(
                        text = "1. AI Content Accuracy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "Although the app aims to provide useful and high-quality educational material, AI-generated content may occasionally contain:"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "• Incorrect information")
                    Text(text = "• Incomplete explanations")
                    Text(text = "• Outdated facts")
                    Text(text = "• Misleading answers")
                    Text(text = "• Formatting or logical mistakes")

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECTION 2

                    Text(
                        text = "2. Educational Assistance Only",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "QuickNotes AI is intended to assist students with studying, revision, brainstorming, and practice preparation."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "The app should not be considered a replacement for teachers, professional educational guidance, official textbooks, or verified academic resources."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECTION 3

                    Text(
                        text = "3. User Responsibility",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "Users are responsible for reviewing and verifying important information before using it in:"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "• Exams")
                    Text(text = "• Assignments")
                    Text(text = "• Academic submissions")
                    Text(text = "• Professional or real-world applications")

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECTION 4

                    Text(
                        text = "4. Dynamic AI Responses",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "AI-generated responses may vary over time. The same prompt can produce different notes, MCQs, or explanations on different occasions."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECTION 5

                    Text(
                        text = "5. MCQ & Quiz Disclaimer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "MCQs and quizzes generated by the app are created automatically using AI systems and are intended for self-practice purposes only."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "Users should independently confirm answers and explanations where accuracy is important."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECTION 6

                    Text(
                        text = "6. Service Limitations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "AI services may occasionally experience delays, incomplete responses, or temporary unavailability depending on internet connection and external AI systems."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECTION 7

                    Text(
                        text = "7. Responsible Usage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "Users are encouraged to use AI responsibly and avoid relying solely on generated content for critical decisions."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "8. Younger Users",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "Younger students should use AI-generated educational content with guidance from parents, guardians, or teachers whenever possible."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Use AI responsibly and verify important information.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}