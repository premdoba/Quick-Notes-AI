package com.example.quicknotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quicknotes.R
import com.example.quicknotes.data.model.SavedQuiz
import com.example.quicknotes.viewmodel.StudyViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun McqDetailScreen(
    navController: NavController,
    vm: StudyViewModel,
    quizId: Int
) {

    val quiz by vm.getQuizById(quizId).collectAsState(initial = null)

    if (quiz == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Loading...")
        }
        return
    }

    val data = quiz!!

    val type = object : TypeToken<List<SavedQuiz>>() {}.type
    val mcqList: List<SavedQuiz> = Gson().fromJson(data.quizJson, type)

    val score = mcqList.count { it.answer.trim() == it.selectedAnswer?.trim() }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(data.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            mcqList.forEachIndexed { index, mcq ->

                val selected = mcq.selectedAnswer ?: ""

                val correct = selected.startsWith(mcq.answer.trim()) ||
                        selected.startsWith("${mcq.answer.trim()})")

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            if (correct)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            else
                                MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                    )
                ) {

                    Column(modifier = Modifier.padding(14.dp)) {

                        Text(
                            text = "${index + 1}) ${mcq.question}",
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Your Answer: ${mcq.selectedAnswer ?: "Not Answered"}")

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
        }
    }
}