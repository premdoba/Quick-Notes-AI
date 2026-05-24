package com.example.quicknotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quicknotes.R
import com.example.quicknotes.data.AppDatabase
import com.example.quicknotes.ui.Routes
import com.example.quicknotes.viewmodel.Mcq
import com.example.quicknotes.viewmodel.StudyNotes
import com.example.quicknotes.viewmodel.StudyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    navController: NavController,
    vm: StudyViewModel,
    id: Int
) {

    val context = navController.context
    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth >= 600

    val paddingSize = if (isTablet) 32.dp else 16.dp
    val maxContentWidth = 720.dp

    var notes by remember { mutableStateOf<StudyNotes?>(null) }

    // NEW LOADING STATE
    var isQuizLoading by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        )
    )

    LaunchedEffect(id) {

        scope.launch {

            val dao = AppDatabase.getDatabase(context).studyDao()

            val item = withContext(Dispatchers.IO) {
                dao.getById(id)
            }

            if (item != null) {

                // RESTORE VIEWMODEL VALUES
                vm.lastInputText = item.originalInput
                vm.lastLevelText = item.educationLevel

                val loadedNotes = StudyNotes(
                    shortNotes = item.shortNotes,

                    importantQuestions = item.questions,

                    mcqs = parseMcqsFromRaw(item.mcqsRaw),

                    mcqsRaw = item.mcqsRaw,

                    summary = item.summary
                )

                // LOCAL STATE
                notes = loadedNotes

                // VIEWMODEL STATE
                vm.loadHistoryNotes(loadedNotes)
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Study Material",
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
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(paddingValues)
                .padding(paddingSize)
                .verticalScroll(rememberScrollState()),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = maxContentWidth)
            ) {

                if (notes == null) {

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                } else {

                    SectionCard(
                        "Short Notes",
                        notes!!.shortNotes
                    )

                    SectionCard(
                        "Important Questions",
                        notes!!.importantQuestions
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
                                    painter = painterResource(
                                        id = R.drawable.baseline_quiz_24
                                    ),

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

                                        color = MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.7f
                                        )
                                    )
                                }
                            }

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.1f
                                )
                            )

                            Button(
                                onClick = {

                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Turn on internet to generate fresh MCQs"
                                        )
                                    }

                                    isQuizLoading = true

                                    vm.refreshMcqs(
                                        vm.lastInputText,
                                        vm.lastMcqDifficulty
                                    )

                                    scope.launch {

                                        kotlinx.coroutines.delay(2500)

                                        isQuizLoading = false

                                        navController.navigate(
                                            Routes.Quiz.route
                                        )
                                    }
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

                    SectionCard(
                        "Summary",
                        notes!!.summary
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

fun parseMcqsFromRaw(raw: String): List<Mcq> {

    val blocks = raw.split(
        Regex("""\n(?=\d+\))""")
    )

    val list = mutableListOf<Mcq>()

    for (block in blocks) {

        val lines = block.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (lines.size < 6) continue

        val qLine = lines.firstOrNull {
            it.contains(")")
        } ?: continue

        val question = qLine.substringAfter(")").trim()

        val options = lines.filter {

            it.startsWith("A)") ||
                    it.startsWith("B)") ||
                    it.startsWith("C)") ||
                    it.startsWith("D)")
        }

        val answerLine = lines.find {
            it.startsWith("Answer:")
        } ?: ""

        val answer = answerLine
            .substringAfter("Answer:")
            .trim()

        val explanationLine = lines.find {
            it.startsWith("Explanation:")
        } ?: ""

        val explanation = explanationLine
            .substringAfter("Explanation:")
            .trim()

        if (
            question.isNotBlank() &&
            options.size >= 4
        ) {

            list.add(
                Mcq(
                    question = question,

                    options = options,

                    answer = answer.ifBlank {
                        "Not given"
                    },

                    explanation = explanation.ifBlank {
                        "No explanation given."
                    }
                )
            )
        }
    }

    return list
}