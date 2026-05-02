package com.example.quicknotes

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknotes.data.AppDatabase
import com.example.quicknotes.data.StudyHistoryEntity
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Mcq(
    val question: String,
    val options: List<String>,
    val answer: String
)

data class StudyNotes(
    val shortNotes: String = "",
    val importantQuestions: String = "",
    val mcqs: List<Mcq> = emptyList(),
    val mindmapSummary: String = "",
    val summary: String = "",
    val mcqsRaw: String = ""
)

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val notes: StudyNotes) : UiState()
    data class Error(val message: String) : UiState()
}

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.studyDao()

    val historyList = dao.getAllHistory()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3-flash-preview",
        apiKey = "API_KEY"
    )

    fun generateStudyMaterial(input: String, educationLevel: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val limitedInput = input.take(8000)

                val prompt = """
                You are an expert teacher.
                
                Student Education Level / Grade: "$educationLevel"
                
                Generate study material based on the below content.
                Keep the language VERY EASY and simple.
                
                VERY IMPORTANT RULES:
                - Short notes must NEVER be in paragraph.
                - Short notes must ALWAYS be in bullet points.
                - Each bullet point should be maximum 1-2 lines.
                - Use simple and easy English words.
                - Do not use markdown like **bold**, ###, etc.
                - Do not use emojis.
                - Do not add any extra headings.
                - Output must be clean and readable.
                - Follow the exact format strictly.
                
                Content:
                "$limitedInput"
                
                Return output ONLY in this exact format:
                
                [SHORT NOTES]
                - point
                - point
                - point
                
                [QUESTIONS]
                1.
                2.
                3.
                4.
                5.
                
                [MCQS]
                1) Question
                A) option
                B) option
                C) option
                D) option
                Answer: A
                
                (repeat 10 mcqs)
                
                [MINDMAP]
                - point
                - point
                - point
                
                [SUMMARY]
                - point
                - point
                - point
                """.trimIndent()

                val responseText = withContext(Dispatchers.IO) {
                    val response = generativeModel.generateContent(prompt)
                    response.text ?: ""
                }

                if (responseText.isBlank()) {
                    _uiState.value = UiState.Error("Empty response received from AI.")
                } else {
                    val notes = parseNotes(responseText)
                    _uiState.value = UiState.Success(notes)

                    dao.insertHistory(
                        StudyHistoryEntity(
                            topic = limitedInput.take(35),
                            educationLevel = educationLevel,
                            shortNotes = notes.shortNotes,
                            questions = notes.importantQuestions,
                            mcqsRaw = notes.mcqsRaw,
                            mindmap = notes.mindmapSummary,
                            summary = notes.summary
                        )
                    )
                }

            } catch (e: Exception) {
                Log.e("GEMINI_ERROR", "Gemini API Failed", e)
                _uiState.value = UiState.Error(e.localizedMessage ?: "Something went wrong.")
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }

    private fun safeExtract(text: String, start: String, end: String): String {
        return text.substringAfter(start, "")
            .substringBefore(end, "")
            .trim()
    }

    private fun parseMcqs(mcqText: String): List<Mcq> {
        val blocks = mcqText.split(Regex("""\n(?=\d+\))"""))
        val mcqList = mutableListOf<Mcq>()

        for (block in blocks) {
            val lines = block.lines()
                .map { it.trim() }
                .filter { it.isNotBlank() }

            if (lines.size < 6) continue

            val questionLine = lines.firstOrNull { it.contains(")") } ?: continue
            val question = questionLine.substringAfter(")").trim()

            val options = lines.filter {
                it.startsWith("A)") || it.startsWith("B)") || it.startsWith("C)") || it.startsWith("D)")
            }

            val answerLine = lines.find { it.startsWith("Answer:") } ?: ""
            val answer = answerLine.substringAfter("Answer:").trim()

            if (question.isNotBlank() && options.size >= 4) {
                mcqList.add(
                    Mcq(
                        question = question,
                        options = options,
                        answer = answer.ifBlank { "Not given" }
                    )
                )
            }
        }

        return mcqList
    }

    private fun parseNotes(text: String): StudyNotes {
        val mcqRaw = safeExtract(text, "[MCQS]", "[MINDMAP]")

        return StudyNotes(
            shortNotes = safeExtract(text, "[SHORT NOTES]", "[QUESTIONS]"),
            importantQuestions = safeExtract(text, "[QUESTIONS]", "[MCQS]"),
            mcqs = parseMcqs(mcqRaw),
            mindmapSummary = safeExtract(text, "[MINDMAP]", "[SUMMARY]"),
            summary = text.substringAfter("[SUMMARY]", "").trim(),
            mcqsRaw = mcqRaw
        )
    }
}