package com.example.quicknotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quicknotes.R
import com.example.quicknotes.viewmodel.Mcq
import com.example.quicknotes.viewmodel.StudyNotes
import com.example.quicknotes.viewmodel.StudyViewModel
import com.example.quicknotes.data.AppDatabase
import com.example.quicknotes.ui.Routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(navController: NavController, vm: StudyViewModel, id: Int) {

    val context = navController.context
    val scope = rememberCoroutineScope()

    var notes by remember { mutableStateOf<StudyNotes?>(null) }
    var educationLevel by remember { mutableStateOf("") }

    // Store mcqs separately so we can refresh only mcqs
    var mcqsParsed by remember { mutableStateOf<List<Mcq>>(emptyList()) }

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
            val item = withContext(Dispatchers.IO) { dao.getById(id) }

            if (item != null) {
                educationLevel = item.educationLevel

                val loadedNotes = StudyNotes(
                    shortNotes = item.shortNotes,
                    importantQuestions = item.questions,
                    mcqs = emptyList(),
                    mcqsRaw = item.mcqsRaw,
                    summary = item.summary
                )

                notes = loadedNotes
                mcqsParsed = parseMcqsFromRaw(loadedNotes.mcqsRaw)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Study Material", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            if (notes == null) {
                CircularProgressIndicator()
            } else {

                SectionCard("Short Notes", notes!!.shortNotes)
                SectionCard("Important Questions", notes!!.importantQuestions)

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

//                mcqsParsed.forEachIndexed { index, mcq ->
//                    key(index, mcq.question) {
//                        QuizMcqCard(index + 1, mcq)
//                    }
//                }


                Spacer(modifier = Modifier.height(10.dp))

                SectionCard("Summary", notes!!.summary)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

fun parseMcqsFromRaw(raw: String): List<Mcq> {
    val blocks = raw.split(Regex("""\n(?=\d+\))"""))
    val list = mutableListOf<Mcq>()

    for (block in blocks) {
        val lines = block.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (lines.size < 6) continue

        val qLine = lines.firstOrNull { it.contains(")") } ?: continue
        val question = qLine.substringAfter(")").trim()

        val options = lines.filter {
            it.startsWith("A)") || it.startsWith("B)") || it.startsWith("C)") || it.startsWith("D)")
        }

        val answerLine = lines.find { it.startsWith("Answer:") } ?: ""
        val answer = answerLine.substringAfter("Answer:").trim()

        val explanationLine = lines.find { it.startsWith("Explanation:") } ?: ""
        val explanation = explanationLine.substringAfter("Explanation:").trim()

        if (question.isNotBlank() && options.size >= 4) {
            list.add(
                Mcq(
                    question = question,
                    options = options,
                    answer = answer.ifBlank { "Not given" },
                    explanation = explanation.ifBlank { "No explanation given." }
                )
            )
        }
    }

    return list
}