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
                        text = "QuickNotes AI Terms & Conditions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text =
                            "By using QuickNotes AI, you agree to the following terms and conditions. Please read them carefully before using the application.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // SECTION 1

                    Text(
                        text = "1. Educational Purpose",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "QuickNotes AI is designed for educational, learning, revision, and productivity purposes only. The app helps students generate notes, important questions, MCQs, and manage study tasks."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECTION 2

                    Text(
                        text = "2. AI Generated Content",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "The app uses AI-generated responses based on user prompts. While we try to provide useful and relevant educational content, generated notes and answers may occasionally contain mistakes, incomplete information, or inaccuracies."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "Users are responsible for verifying important academic information before relying on it for exams, assignments, or professional work."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECTION 3

                    Text(
                        text = "3. User Responsibilities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "You agree to use the app responsibly and lawfully. Users must not:"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "• Misuse or exploit the application")
                    Text(text = "• Attempt unauthorized access to services")
                    Text(text = "• Use the app for harmful or illegal activities")
                    Text(text = "• Abuse AI generation systems")

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECTION 4

                    Text(
                        text = "4. User Accounts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "Some features of QuickNotes AI may require users to create an account using email and password authentication."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "Users are responsible for maintaining the confidentiality of their login credentials and activities performed under their account."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECTION 5

                    Text(
                        text = "5. Local Data Storage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "QuickNotes AI stores notes, MCQ results, and todo data locally on your device to support offline access and a better user experience."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "Users are responsible for managing and backing up their important data if necessary."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECTION 6

                    Text(
                        text = "6. Service Availability",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "Some app features may require an internet connection, especially AI-based services. OCR (text extraction) and AI processing may depend on device performance and external services."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "Certain features may rely on third-party services such as authentication providers and AI APIs."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECTION 7

                    Text(
                        text = "7. Updates & Changes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "Features, design, functionality, and terms may change or improve over time without prior notice."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "Continued use of the application after updates means you accept the revised terms."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECTION 8

                    Text(
                        text = "8. Limitation of Liability",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "QuickNotes AI and its developer shall not be held responsible for any direct or indirect loss resulting from the use of AI-generated content or application services."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECTION 9

                    Text(
                        text = "9. Contact",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "For support, account-related requests, data deletion requests, questions, or feedback regarding these terms, users may contact the developer through the Contact Us section inside the application."
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // SECTION 10

                    Text(
                        text = "10. Camera, Images & Document Usage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "QuickNotes AI allows users to capture images using the camera, select images from the gallery, and upload PDF documents for educational purposes."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "These inputs are processed only for extracting text and generating study-related content within the app."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text =
                            "• Camera and gallery images are used only for OCR (text extraction)\n" +
                                    "• PDF files are used only for generating notes and summaries\n" +
                                    "• No image or document data is used for advertising or tracking\n" +
                                    "• Processing may be performed using on-device or third-party services"
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