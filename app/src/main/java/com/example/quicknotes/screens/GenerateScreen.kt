package com.example.quicknotes.ui.screens

import android.Manifest
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.quicknotes.viewmodel.Mcq
import com.example.quicknotes.R
import com.example.quicknotes.viewmodel.StudyViewModel
import com.example.quicknotes.viewmodel.UiState
import com.example.quicknotes.ui.Routes
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(navController: NavController, vm: StudyViewModel) {

    val context = navController.context

    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var inputText by remember { mutableStateOf("") }
    var levelText by remember { mutableStateOf("") }
    var fabExpanded by remember { mutableStateOf(false) }

    val state by vm.uiState.collectAsState()

    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        )
    )

    // Create temp file for camera photo
    val photoFile = remember {
        File.createTempFile("camera_photo_", ".jpg", context.cacheDir)
    }

    // Uri for camera output
    val cameraImageUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            photoFile
        )
    }

    // Camera capture
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                selectedImageUri = cameraImageUri

                extractTextFromImage(cameraImageUri, context) { extractedText ->
                    inputText = extractedText
                }
            }
        }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                cameraLauncher.launch(cameraImageUri)
            } else {
                inputText = "Camera Permission Denied!"
            }
        }

    // Gallery picker
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri

                extractTextFromImage(uri, context) { extractedText ->
                    inputText = extractedText
                }
            }
        }

    val galleryPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                galleryLauncher.launch("image/*")
            } else {
                inputText = "Gallery Permission Denied!"
            }
        }

    // PDF picker
    val pdfLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedPdfUri = uri
                inputText = "Selected PDF: $uri"
            }
        }

    Scaffold(
        floatingActionButton = {

            Box(modifier = Modifier.wrapContentSize()) {

                Column(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.End
                ) {

                    AnimatedVisibility(visible = fabExpanded) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.End
                        ) {

                            MiniFabButton("Camera", R.drawable.camera) {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                fabExpanded = false
                            }

                            MiniFabButton("Gallery", R.drawable.baseline_image) {
                                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                fabExpanded = false
                            }

                            MiniFabButton("PDF", R.drawable.baseline_picture_as_pdf_24) {
                                pdfLauncher.launch("application/pdf")
                                fabExpanded = false
                            }
                        }
                    }

                    FloatingActionButton(
                        onClick = { fabExpanded = !fabExpanded },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
        },

        bottomBar = {
            BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {

                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Routes.History.route) },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_history),
                            contentDescription = "History"
                        )
                    },
                    label = { Text("History") }
                )
            }
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = "QuickNotes AI",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Generate study notes, MCQs instantly",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = levelText,
                        onValueChange = { levelText = it },
                        label = { Text("Education Level") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        label = { Text("Paste Notes / Topic") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (inputText.isNotBlank() && levelText.isNotBlank()) {
                                vm.generateStudyMaterial(inputText, levelText)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Generate Study Material")
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            when (state) {

                is UiState.Loading -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Success -> {
                    val notes = (state as UiState.Success).notes

                    SectionCard("Short Notes", notes.shortNotes)
                    SectionCard("Important Questions", notes.importantQuestions)

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_quiz_24),
                                    contentDescription = "Quiz",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )

                                Column {
                                    Text(
                                        text = "MCQ Quiz",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = "Test your knowledge with 10 questions",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                            Button(
                                onClick = {
                                    vm.refreshMcqs(vm.lastInputText, vm.lastLevelText)
                                    navController.navigate(Routes.Quiz.route)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Start Quiz")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

//                    notes.mcqs.forEachIndexed { index, mcq ->
//                        key(index, mcq.question) {
//                            QuizMcqCard(index + 1, mcq)
//                        }
//                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SectionCard("Summary", notes.summary)
                }

                is UiState.Error -> {
                    val msg = (state as UiState.Error).message

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                text = "Error Occurred!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                else -> {}
            }

            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
fun MiniFabButton(
    text: String,
    icon: Int,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        text = { Text(text) },
        icon = { Icon(painter = painterResource(id = icon), contentDescription = text) },
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun SectionCard(title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )
        }
    }
}

//@Composable
//fun QuizMcqCard(number: Int, mcq: Mcq) {
//
//    var selectedOption by remember { mutableStateOf<String?>(null) }
//    var submitted by remember { mutableStateOf(false) }
//
//    val correctAnswer = mcq.answer.trim()
//
//    fun isCorrect(option: String): Boolean {
//        return option.startsWith(correctAnswer) || option.startsWith("$correctAnswer)")
//    }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(bottom = 12.dp),
//        shape = RoundedCornerShape(20.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//        elevation = CardDefaults.cardElevation(8.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//
//            Text(
//                text = "$number) ${mcq.question}",
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold
//            )
//
//            Spacer(modifier = Modifier.height(6.dp))
//
//            mcq.options.forEach { option ->
//
//                val cleanOption = option.substringAfter(")").trim()
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable(enabled = !submitted) {
//                            selectedOption = option
//                        }
//                        .padding(vertical = 2.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    RadioButton(
//                        selected = selectedOption == option,
//                        onClick = {
//                            if (!submitted) selectedOption = option
//                        },
//                        enabled = !submitted
//                    )
//
//                    Spacer(modifier = Modifier.width(6.dp))
//
//                    Text(
//                        text = cleanOption,
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(10.dp))
//
//            Button(
//                onClick = { submitted = true },
//                enabled = selectedOption != null && !submitted,
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(14.dp)
//            ) {
//                Text("Submit Answer")
//            }
//
//            if (submitted && selectedOption != null) {
//
//                val correct = isCorrect(selectedOption!!)
//
//                Spacer(modifier = Modifier.height(10.dp))
//
//                Text(
//                    text = if (correct) "✅ Correct Answer!" else "❌ Wrong Answer!",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold,
//                    color = if (correct) MaterialTheme.colorScheme.primary
//                    else MaterialTheme.colorScheme.error
//                )
//
//                Spacer(modifier = Modifier.height(6.dp))
//
//                Text(
//                    text = "Correct Answer: ${mcq.answer}",
//                    style = MaterialTheme.typography.bodyMedium,
//                    fontWeight = FontWeight.Bold
//                )
//
//                // Explanation / Description
//                if (mcq.explanation.isNotBlank()) {
//                    Spacer(modifier = Modifier.height(6.dp))
//
//                    Text(
//                        text = "Explanation: ${mcq.explanation}",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
//                    )
//                }
//            }
//        }
//    }
//}

// OCR function
fun extractTextFromImage(uri: Uri, context: android.content.Context, onResult: (String) -> Unit) {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val image = InputImage.fromBitmap(bitmap, 0)

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                onResult(visionText.text)
            }
            .addOnFailureListener { e ->
                onResult("OCR Failed: ${e.message}")
            }

    } catch (e: Exception) {
        onResult("Error: ${e.message}")
    }
}