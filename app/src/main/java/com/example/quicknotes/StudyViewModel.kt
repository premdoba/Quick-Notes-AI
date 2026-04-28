package com.example.quicknotes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class StudyNotes(
    val shortNotes: String = "",
    val importantQuestions: String = "",
    val mcqs: String = "",
    val mindmapSummary: String = "",
    val revisionPlan: String = ""
)

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val notes: StudyNotes) : UiState()
    data class Error(val message: String) : UiState()
}

class StudyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3-flash-preview",
        apiKey = "API_KEY"
    )

    fun generateStudyMaterial(input: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val prompt = """
                    Act as an expert educator. Based on the following content:
                    "$input"

                    Please provide the following sections exactly using these headers:
                    [SHORT NOTES]
                    (Exam-focused notes)

                    [QUESTIONS]
                    (5 important questions)

                    [MCQS]
                    (5 MCQs with answers)

                    [MINDMAP]
                    (Bullet-point mindmap)

                    [PLAN]
                    (1-day and 7-day revision plan)
                """.trimIndent()

                val responseText = withContext(Dispatchers.IO) {
                    val response = generativeModel.generateContent(prompt)
                    response.text ?: ""
                }

                if (responseText.isBlank()) {
                    _uiState.value = UiState.Error("Empty response received from AI.")
                } else {
                    _uiState.value = UiState.Success(parseNotes(responseText))
                }

            } catch (e: Exception) {
                Log.e("GEMINI_ERROR", "Gemini API Failed", e)
                _uiState.value = UiState.Error(
                    e.localizedMessage ?: "Something went wrong while generating notes."
                )
            }
        }
    }

    private fun safeExtract(text: String, start: String, end: String): String {
        return text.substringAfter(start, "")
            .substringBefore(end, "")
            .trim()
    }

    private fun parseNotes(text: String): StudyNotes {
        return StudyNotes(
            shortNotes = safeExtract(text, "[SHORT NOTES]", "[QUESTIONS]"),
            importantQuestions = safeExtract(text, "[QUESTIONS]", "[MCQS]"),
            mcqs = safeExtract(text, "[MCQS]", "[MINDMAP]"),
            mindmapSummary = safeExtract(text, "[MINDMAP]", "[PLAN]"),
            revisionPlan = text.substringAfter("[PLAN]", "").trim()
        )
    }
}