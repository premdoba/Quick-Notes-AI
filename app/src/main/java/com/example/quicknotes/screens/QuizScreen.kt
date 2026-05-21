package com.example.quicknotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quicknotes.viewmodel.Mcq
import com.example.quicknotes.viewmodel.StudyViewModel
import com.example.quicknotes.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(navController: NavController, vm: StudyViewModel) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth >= 600
    val paddingSize = if (isTablet) 32.dp else 16.dp
    val maxContentWidth = 720.dp

    val state by vm.uiState.collectAsState()

    if (state !is UiState.Success) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No quiz available. Please generate first.")
        }
        return
    }

    val notes = (state as UiState.Success).notes
    val mcqs = notes.mcqs

    var currentIndex by remember { mutableStateOf(0) }
    var submitted by remember { mutableStateOf(false) }

    val selectedAnswers = remember { mutableStateMapOf<Int, String>() }

    fun isCorrect(mcq: Mcq, selected: String?): Boolean {
        if (selected == null) return false
        val correctAnswer = mcq.answer.trim()
        return selected.startsWith(correctAnswer) || selected.startsWith("$correctAnswer)")
    }

    val score = mcqs.countIndexed { index, mcq ->
        isCorrect(mcq, selectedAnswers[index])
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
                .padding(padding)
                .padding(paddingSize),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = maxContentWidth)
            ) {

                if (!submitted) {

                    val mcq = mcqs[currentIndex]

                    Text(
                        text = "Question ${currentIndex + 1} / ${mcqs.size}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = mcq.question,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    mcq.options.forEach { option ->

                        val cleanOption = option.substringAfter(")").trim()

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = selectedAnswers[currentIndex] == option,
                                onClick = {
                                    selectedAnswers[currentIndex] = option
                                }
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(cleanOption)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Button(
                            onClick = { currentIndex-- },
                            enabled = currentIndex > 0
                        ) {
                            Text("Back")
                        }

                        if (currentIndex < mcqs.size - 1) {
                            Button(
                                onClick = { currentIndex++ },
                                enabled = currentIndex < mcqs.size - 1
                            ) {
                                Text("Next")
                            }
                        } else {
                            Button(
                                onClick = { submitted = true },
                                enabled = selectedAnswers.size > 0
                            ) {
                                Text("Submit Quiz")
                            }
                        }
                    }
                } else {

                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {

                        Text(
                            text = "Quiz Result",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Score: $score / ${mcqs.size}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        mcqs.forEachIndexed { index, mcq ->

                            val selected = selectedAnswers[index]
                            val correct = isCorrect(mcq, selected)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (correct)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    else MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {

                                    Text(
                                        text = "${index + 1}) ${mcq.question}",
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text("Your Answer: ${selected ?: "Not Answered"}")

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Correct Answer: ${mcq.answer}",
                                        fontWeight = FontWeight.Bold
                                    )

                                    if (mcq.explanation.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Explanation: ${mcq.explanation}")
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                vm.refreshMcqs(vm.lastInputText, vm.lastMcqDifficulty)
                                navController.popBackStack()
                                navController.navigate("quiz")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Make New Quiz")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                navController.popBackStack()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Return to Generate Screen")
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

fun <T> List<T>.countIndexed(predicate: (Int, T) -> Boolean): Int {
    var count = 0
    forEachIndexed { index, item ->
        if (predicate(index, item)) count++
    }
    return count
}