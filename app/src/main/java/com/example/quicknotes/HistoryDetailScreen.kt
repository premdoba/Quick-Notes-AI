package com.example.quicknotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quicknotes.Mcq
import com.example.quicknotes.StudyNotes
import com.example.quicknotes.StudyViewModel
import com.example.quicknotes.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(navController: NavController, vm: StudyViewModel, id: Int) {

    val context = navController.context
    val scope = rememberCoroutineScope()

    var notes by remember { mutableStateOf<StudyNotes?>(null) }

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
                notes = StudyNotes(
                    shortNotes = item.shortNotes,
                    importantQuestions = item.questions,
                    mcqs = emptyList(),
                    mcqsRaw = item.mcqsRaw,
                    mindmapSummary = item.mindmap,
                    summary = item.summary
                )
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

                // MCQ RAW SHOW AS FLASHCARDS SIMPLE FORMAT
                val mcqsParsed = parseMcqsFromRaw(notes!!.mcqsRaw)

                Text(
                    text = "MCQs Flashcards",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                mcqsParsed.forEachIndexed { index, mcq ->
                    FlashCardMcq(index + 1, mcq)
                }

                Spacer(modifier = Modifier.height(10.dp))

                SectionCard("Mindmap", notes!!.mindmapSummary)

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

        if (question.isNotBlank() && options.size >= 4) {
            list.add(Mcq(question, options, answer))
        }
    }

    return list
}