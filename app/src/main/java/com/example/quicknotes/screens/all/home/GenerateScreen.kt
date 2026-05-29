package com.example.quicknotes.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.quicknotes.R
import com.example.quicknotes.settings.SettingsViewModel
import com.example.quicknotes.ui.Routes
import com.example.quicknotes.viewmodel.StudyViewModel
import com.example.quicknotes.viewmodel.UiState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(navController: NavController, vm: StudyViewModel, settingsVm: SettingsViewModel) {

    val context = navController.context

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth >= 600
    val paddingSize = if (isTablet) 32.dp else 16.dp
    val maxContentWidth = 720.dp

    var isGenerating by remember { mutableStateOf(false) }
    var isQuizLoading by remember { mutableStateOf(false) }
    var isDoubtLoading by remember { mutableStateOf(false) }

    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var inputText by remember { mutableStateOf("") }

    val defaultEducation by settingsVm.education.collectAsState()

    val defaultMcq by settingsVm.mcq.collectAsState()

    var levelText by remember(defaultEducation) {
        mutableStateOf(defaultEducation)
    }

    var mcqDifficulty by remember(defaultMcq) {
        mutableStateOf(defaultMcq)
    }

    var fabExpanded by remember { mutableStateOf(false) }

    var doubtText by remember { mutableStateOf("") }
    var doubtAnswer by remember { mutableStateOf("") }

    val state by vm.uiState.collectAsState()

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

    LaunchedEffect(Unit) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            notificationPermissionLauncher.launch(
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    LaunchedEffect(state) {
        when (state) {
            is UiState.Success,
            is UiState.Error -> {
                isGenerating = false
            }

            else -> {}
        }
    }

    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        )
    )

    var cameraImageUriString by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val cameraImageUri =
        cameraImageUriString?.let {
            Uri.parse(it)
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->

            if (success && cameraImageUri != null) {

                extractTextFromImage(
                    cameraImageUri!!,
                    context
                ) { text ->

                    inputText = text
                }

            } else {

                inputText = "Camera capture failed"
            }
        }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                try {

                    val photoFile = File(
                        context.cacheDir,
                        "camera_${System.currentTimeMillis()}.jpg"
                    )

                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        photoFile
                    )

                    cameraImageUriString = uri.toString()

                    cameraLauncher.launch(uri)

                } catch (e: Exception) {

                    e.printStackTrace()

                    inputText = "Camera Error: Please try again later once issue is solved"
                }

            } else {

                inputText = "Camera permission denied"
            }
        }



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

    val pdfLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedPdfUri = uri
                extractTextFromPdf(uri, context) { text ->
                    inputText = text
                }
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "QuickNotes AI",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Generate study notes, MCQs instantly",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            )
        },

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

                                cameraPermissionLauncher.launch(
                                    Manifest.permission.CAMERA
                                )

                                fabExpanded = false
                            }
                            MiniFabButton("Gallery", R.drawable.baseline_image) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                                    galleryPermissionLauncher.launch(
                                        Manifest.permission.READ_MEDIA_IMAGES
                                    )

                                } else {

                                    galleryPermissionLauncher.launch(
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                    )
                                }

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
                        Icon(painter = painterResource(R.drawable.baseline_add_24), contentDescription = "Add")
                    }
                }
            }
        },

        bottomBar = {
            if (!isTablet) {
                BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {

                    NavigationBarItem(
                        selected = true,
                        onClick = { navController.navigate(Routes.Generate.route) },
                        icon = { Icon(painter = painterResource(R.drawable.outline_home_24), contentDescription = "Home") },
                        label = { Text("Home") },
                        colors = NavigationBarItemDefaults.colors(

                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            indicatorColor = MaterialTheme.colorScheme.primary,

                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate(Routes.Downloads.route) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.outline_download_24),
                                contentDescription = "Downloads"
                            )
                        },
                        label = { Text("Downloads") },
                        colors = NavigationBarItemDefaults.colors(

                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            indicatorColor = MaterialTheme.colorScheme.primary,

                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate(Routes.QuickTodo.route) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_checklist_24),
                                contentDescription = "QuickTodo"
                            )
                        },
                        label = { Text("Todo") },
                        colors = NavigationBarItemDefaults.colors(

                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            indicatorColor = MaterialTheme.colorScheme.primary,

                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate(Routes.Settings.route) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.outline_settings_24),
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings") },
                        colors = NavigationBarItemDefaults.colors(

                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            indicatorColor = MaterialTheme.colorScheme.primary,

                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

    ) { paddingValues ->

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(paddingValues)
        ) {

            if (isTablet) {

                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {

                    NavigationRailItem(
                        selected = true,
                        onClick = {},
                        icon = {
                            Icon(painter = painterResource(R.drawable.outline_home_24), contentDescription = "Home")
                        },
                        label = { Text("Home") },
                        colors = NavigationRailItemDefaults.colors(

                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            indicatorColor = MaterialTheme.colorScheme.primary,

                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    NavigationRailItem(
                        selected = false,
                        onClick = { navController.navigate(Routes.Downloads.route) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_history),
                                contentDescription = "History"
                            )
                        },
                        label = { Text("History") },
                        colors = NavigationRailItemDefaults.colors(

                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            indicatorColor = MaterialTheme.colorScheme.primary,

                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    NavigationRailItem(
                        selected = false,
                        onClick = { navController.navigate(Routes.QuickTodo.route) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_checklist_24),
                                contentDescription = "QuickTodo"
                            )
                        },
                        label = { Text("Todo") },
                        colors = NavigationRailItemDefaults.colors(

                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            indicatorColor = MaterialTheme.colorScheme.primary,

                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    NavigationRailItem(
                        selected = false,
                        onClick = {
                            navController.navigate(Routes.Settings.route)
                        },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.outline_settings_24),
                                contentDescription = "Settings"
                            )
                        },
                        label = {
                            Text("Settings")
                        },
                        colors = NavigationRailItemDefaults.colors(

                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            indicatorColor = MaterialTheme.colorScheme.primary,

                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingSize)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = maxContentWidth)
                ) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(10.dp)
                    ) {

                        Column(modifier = Modifier.padding(16.dp)) {

                            EducationLevelDropdown(
                                label = "Education Level",
                                selectedValue = levelText,
                                options = listOf(
                                    "5th - 8th",
                                    "9th - 10th",
                                    "11th - 12th",
                                    "Graduation",
                                    "Post Graduation"
                                ),
                                onSelected = { levelText = it }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            EducationLevelDropdown(
                                label = "MCQ Difficulty",
                                selectedValue = mcqDifficulty,
                                options = listOf("Easy", "Medium", "Hard"),
                                onSelected = { mcqDifficulty = it }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                label = { Text("Enter Topic") },
                                placeholder = {Text("eg. Photosynthesis, Rotational Motion, etc.")},
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
                                    if (
                                        inputText.isNotBlank() &&
                                        levelText.isNotBlank()
                                    ) {
                                        isGenerating = true
                                        vm.generateStudyMaterial(inputText, levelText)
                                    }
                                },
                                enabled = !isGenerating,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {

                                if (isGenerating) {

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {

                                        LinearProgressIndicator(
                                            modifier = Modifier
                                                .width(80.dp)
                                                .height(6.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }

                                } else {

                                    Text("Generate Study Material")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    when (state) {

                        is UiState.Loading -> {}

                        is UiState.Success -> {

                            val notes = (state as UiState.Success).notes

                            SectionCard("Short Notes", notes.shortNotes)

                            SectionCard(
                                "Important Questions",
                                notes.importantQuestions
                            )

                            Spacer(modifier = Modifier.height(10.dp))

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
                                                text = "Test your knowledge with questions",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.7f
                                                )
                                            )
                                        }
                                    }

                                    Divider()

                                    Button(
                                        onClick = {

                                            isQuizLoading = true

                                            val finalDifficulty =
                                                if (mcqDifficulty.isBlank()) {
                                                    "Medium"
                                                } else {
                                                    mcqDifficulty
                                                }

                                            vm.refreshMcqs(
                                                vm.lastInputText,
                                                finalDifficulty
                                            )

                                            navController.navigate(Routes.Quiz.route)

                                            isQuizLoading = false
                                        },
                                        enabled = !isQuizLoading,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {

                                        if (isQuizLoading) {

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {

                                                LinearProgressIndicator(
                                                    modifier = Modifier
                                                        .width(80.dp)
                                                        .height(6.dp),
                                                    color = MaterialTheme.colorScheme.primary,
                                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                                )
                                            }

                                        } else {

                                            Text("Start Quiz")
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            SectionCard("Summary", notes.summary)

                            Spacer(modifier = Modifier.height(14.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {

                                Column(modifier = Modifier.padding(16.dp)) {

                                    Text(
                                        text = "Ask Doubts (Continue Chat)",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = doubtText,
                                        onValueChange = { doubtText = it },
                                        label = { Text("Ask your question") },
                                        keyboardOptions = KeyboardOptions(
                                            imeAction = ImeAction.Done
                                        ),
                                        placeholder = { Text("eg. give me an example.") },

                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {

                                            if (doubtText.isNotBlank()) {

                                                isDoubtLoading = true

                                                vm.askDoubt(doubtText) { answer ->

                                                    doubtAnswer = answer
                                                    isDoubtLoading = false
                                                }
                                            }
                                        },
                                        enabled = !isDoubtLoading,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {

                                        if (isDoubtLoading) {

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {

                                                LinearProgressIndicator(
                                                    modifier = Modifier
                                                        .width(80.dp)
                                                        .height(6.dp),
                                                    color = MaterialTheme.colorScheme.primary,
                                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                                )
                                            }

                                        } else {

                                            Text("Ask AI")
                                        }
                                    }

                                    if (doubtAnswer.isNotBlank()) {

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = doubtAnswer,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.9f
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        is UiState.Error -> {

                            val msg =
                                if (!vm.isInternetAvailable(context)) {
                                    "No internet connection detected. Please check your network and try again."
                                } else {
                                    "We're receiving too many requests right now. Please try again after some time."
                                }

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
        icon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text
            )
        },
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationLevelDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {

        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,

            label = {
                Text(label)
            },

            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },

            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),

            shape = RoundedCornerShape(14.dp),

            colors = OutlinedTextFieldDefaults.colors(

                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,

                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,

                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,

                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,

            onDismissRequest = {
                expanded = false
            },

            modifier = Modifier.background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(14.dp)
            )
        ) {

            options.forEach { option ->

                DropdownMenuItem(

                    text = {

                        Text(
                            text = option,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },

                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.onSurface
                    ),

                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun extractTextFromImage(
    uri: Uri,
    context: Context,
    onResult: (String) -> Unit
) {

    try {

        val image = InputImage.fromFilePath(
            context,
            uri
        )

        val recognizer = TextRecognition.getClient(
            TextRecognizerOptions.DEFAULT_OPTIONS
        )

        recognizer.process(image)

            .addOnSuccessListener { visionText ->

                onResult(
                    if (visionText.text.isBlank()) {
                        "No text detected"
                    } else {
                        visionText.text
                    }
                )
            }

            .addOnFailureListener { e ->

                e.printStackTrace()

                onResult("OCR Failed: ${e.message}")
            }

    } catch (e: Exception) {

        e.printStackTrace()

        onResult("Error: ${e.message}")
    }
}
fun extractTextFromPdf(
    uri: Uri,
    context: Context,
    onResult: (String) -> Unit
) {

    try {

        val fileDescriptor =
            context.contentResolver.openFileDescriptor(uri, "r")

        val pdfRenderer =
            android.graphics.pdf.PdfRenderer(
                fileDescriptor!!
            )

        val recognizer =
            TextRecognition.getClient(
                TextRecognizerOptions.DEFAULT_OPTIONS
            )

        val finalText = StringBuilder()

        for (i in 0 until pdfRenderer.pageCount) {

            val page = pdfRenderer.openPage(i)

            val bitmap = android.graphics.Bitmap.createBitmap(
                page.width,
                page.height,
                android.graphics.Bitmap.Config.ARGB_8888
            )

            page.render(
                bitmap,
                null,
                null,
                android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
            )

            val image = InputImage.fromBitmap(bitmap, 0)

            recognizer.process(image)

                .addOnSuccessListener { visionText ->

                    finalText.append("\n")
                    finalText.append(visionText.text)

                    if (i == pdfRenderer.pageCount - 1) {

                        onResult(
                            if (finalText.isBlank()) {
                                "No text found in PDF"
                            } else {
                                finalText.toString()
                            }
                        )

                        pdfRenderer.close()
                        fileDescriptor.close()
                    }
                }

                .addOnFailureListener { e ->

                    e.printStackTrace()

                    onResult("PDF OCR Failed: ${e.message}")
                }

            page.close()
        }

    } catch (e: Exception) {

        e.printStackTrace()

        onResult("PDF Error: ${e.message}")
    }
}