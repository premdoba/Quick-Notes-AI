package com.example.quicknotes.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknotes.data.AppDatabase
import com.example.quicknotes.data.repository.StudyRepositoryImpl
import com.example.quicknotes.data.repository.QuizRepositoryImpl
import com.example.quicknotes.data.repository.AiRepositoryImpl
import com.example.quicknotes.domain.model.StudyHistory
import com.example.quicknotes.domain.model.QuizHistory
import com.example.quicknotes.domain.model.StudyNotes
import com.example.quicknotes.domain.model.Mcq
import com.example.quicknotes.domain.repository.StudyRepository
import com.example.quicknotes.domain.repository.QuizRepository
import com.example.quicknotes.domain.repository.AiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val notes: StudyNotes) : UiState()
    data class Error(val message: String) : UiState()
}

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val studyRepo: StudyRepository = StudyRepositoryImpl(db.studyDao())
    private val quizRepo: QuizRepository = QuizRepositoryImpl(db.quizHistoryDao())
    private val aiRepo: AiRepository = AiRepositoryImpl()

    val historyList = studyRepo.getAllHistory()
    val quizHistoryList = quizRepo.getAllQuizHistory()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    var lastInputText: String = ""
    var lastLevelText: String = ""
    var lastMcqDifficulty: String = "Medium"

    private val chatHistory = mutableListOf<String>()

    fun generateStudyMaterial(input: String, educationLevel: String) {
        lastInputText = input
        lastLevelText = educationLevel

        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val notes = aiRepo.generateStudyMaterial(input, educationLevel)

                if (notes == null) {
                    _uiState.value = UiState.Error("Empty response received from AI. Please refresh your input.")
                } else {
                    _uiState.value = UiState.Success(notes)

                    chatHistory.clear()
                    chatHistory.add("CONTENT:\n${input.take(8000)}")
                    chatHistory.add("AI STUDY MATERIAL GENERATED")

                    studyRepo.insertHistory(
                        StudyHistory(
                            topic = input.take(35),
                            educationLevel = educationLevel,
                            shortNotes = notes.shortNotes,
                            questions = notes.importantQuestions,
                            mcqsRaw = notes.mcqsRaw,
                            summary = notes.summary,
                            originalInput = input.take(8000)
                        )
                    )
                }

            } catch (e: Exception) {
                Log.e("GEMINI_ERROR", "Gemini API Failed", e)
                _uiState.value = UiState.Error("Server is busy. Please try again.")
            }
        }
    }

    suspend fun getHistoryById(id: Int): StudyHistory? {
        return studyRepo.getById(id)
    }

    fun askDoubt(question: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val contextText = if (currentState is UiState.Success) {
                    """
                    Short Notes:
                    ${currentState.notes.shortNotes}
                    Summary:
                    ${currentState.notes.summary}
                    Important Questions:
                    ${currentState.notes.importantQuestions}
                    """.trimIndent()
                } else ""

                val historyText = chatHistory.joinToString("\n\n")

                val responseText = aiRepo.askDoubt(question, lastLevelText, contextText, historyText)

                if (responseText.isNotBlank() && !responseText.startsWith("Error:")) {
                    chatHistory.add("USER QUESTION:\n$question")
                    chatHistory.add("AI ANSWER:\n$responseText")
                    onResult(responseText)
                } else {
                    onResult(responseText.ifBlank { "No response received. Please try again." })
                }
            } catch (e: Exception) {
                Log.e("DOUBT_ERROR", "Ask Doubt Failed", e)
                onResult("Error: ${e.localizedMessage ?: "Something went wrong"}")
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            studyRepo.deleteAll()
        }
    }

    fun deleteHistoryItem(id: Int) {
        viewModelScope.launch {
            studyRepo.deleteById(id)
        }
    }

    fun insertHistoryItem(item: StudyHistory) {
        viewModelScope.launch {
            studyRepo.insertHistory(item)
        }
    }

    fun refreshMcqs(input: String, difficulty: String) {
        lastInputText = input
        lastMcqDifficulty = difficulty
        viewModelScope.launch {
            try {
                val result = aiRepo.generateMcqs(input, lastLevelText, difficulty)
                
                if (result != null) {
                    val (newMcqs, mcqRaw) = result
                    val currentState = _uiState.value
                    if (currentState is UiState.Success) {
                        val updatedNotes = currentState.notes.copy(mcqs = newMcqs, mcqsRaw = mcqRaw)
                        _uiState.value = UiState.Success(updatedNotes)
                    }
                }
            } catch (e: Exception) {
                Log.e("MCQ_REFRESH_ERROR", "Refresh MCQs Failed", e)
            }
        }
    }

    fun loadHistoryNotes(notes: StudyNotes) {
        _uiState.value = UiState.Success(notes)
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun saveQuizResult(title: String, score: Int, totalQuestions: Int, quizJson: String) {
        viewModelScope.launch {
            quizRepo.insertQuiz(
                QuizHistory(
                    title = title,
                    date = System.currentTimeMillis(),
                    score = score,
                    totalQuestions = totalQuestions,
                    quizJson = quizJson
                )
            )
        }
    }

    fun deleteQuiz(id: Int) {
        viewModelScope.launch {
            quizRepo.deleteQuiz(id)
        }
    }

    fun clearQuizHistory() {
        viewModelScope.launch {
            quizRepo.clearQuizHistory()
        }
    }

    fun saveQuiz(quiz: QuizHistory) {
        viewModelScope.launch {
            quizRepo.insertQuiz(quiz)
        }
    }

    fun getQuizById(id: Int): Flow<QuizHistory?> {
        return quizRepo.getQuizById(id)
    }
}