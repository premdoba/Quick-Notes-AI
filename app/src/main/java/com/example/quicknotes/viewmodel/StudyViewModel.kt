package com.example.quicknotes.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknotes.data.AppDatabase
import com.example.quicknotes.data.study.StudyHistoryEntity
import com.example.quicknotes.BuildConfig
import com.example.quicknotes.data.local.QuizHistoryEntity
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Mcq(
    val question: String,
    val options: List<String>,
    val answer: String,
    val explanation: String = ""
)

data class StudyNotes(
    val shortNotes: String = "",
    val importantQuestions: String = "",
    val mcqs: List<Mcq> = emptyList(),
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
    private val quizHistoryDao = db.quizHistoryDao()

    val historyList = dao.getAllHistory()

    val quizHistoryList = quizHistoryDao.getAllQuizHistory()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    var lastInputText: String = ""
    var lastLevelText: String = ""

    // NEW: store last mcq difficulty
    var lastMcqDifficulty: String = "Medium"

    // NEW: store conversation history for continue chat
    private val chatHistory = mutableListOf<String>()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3.5-flash",
        apiKey = BuildConfig.API_KEY
    )

    fun generateStudyMaterial(input: String, educationLevel: String) {
        lastInputText = input
        lastLevelText = educationLevel

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
                Explanation: short easy explanation
                
                (repeat 10 mcqs)
                
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
                    _uiState.value = UiState.Error("Empty response received from AI. Please referesh your input.")
                } else {
                    val notes = parseNotes(responseText)
                    _uiState.value = UiState.Success(notes)

                    // NEW: reset chat history and store generated content
                    chatHistory.clear()
                    chatHistory.add("CONTENT:\n$limitedInput")
                    chatHistory.add("AI STUDY MATERIAL:\n$responseText")

                    dao.insertHistory(
                        StudyHistoryEntity(
                            topic = limitedInput.take(35),
                            educationLevel = educationLevel,
                            shortNotes = notes.shortNotes,
                            questions = notes.importantQuestions,
                            mcqsRaw = notes.mcqsRaw,
                            summary = notes.summary,
                            originalInput = limitedInput
                        )
                    )
                }

            } catch (e: Exception) {
                Log.e("GEMINI_ERROR", "Gemini API Failed", e)
                _uiState.value = UiState.Error(
                    "Server is busy. Please try again."
                )
//                _uiState.value = UiState.Error("Overload is detected on server , please try again later." ?: "Something went wrong.")
            }
        }
    }

    // NEW: Ask doubt / continue chat
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
                } else {
                    ""
                }

                val historyText = chatHistory.joinToString("\n\n")

                val prompt = """
                You are a helpful teacher.
                
                Student Education Level: "$lastLevelText"
                
                Below is previous generated content and conversation:
                
                $historyText
                
                Extra Context:
                $contextText
                
                Student Question:
                "$question"
                
                Generate study material based on the below content.
                Keep the language VERY EASY and simple.
                
                VERY IMPORTANT RULES:
                - Here make sure u just exaplin the point
                - No need to give questions, mcqs, etc.
                - Just see the context and answer the question in short.
                - Short notes must NEVER be in paragraph.
                - Short notes must ALWAYS be in bullet points.
                - Each bullet point should be maximum 1-2 lines.
                - Use simple and easy English words.
                - Do not use markdown like **bold**, ###, etc.
                - Do not use emojis.
                - Do not add any extra headings.
                - Output must be clean and readable.
                """.trimIndent()

                val responseText = withContext(Dispatchers.IO) {
                    val response = generativeModel.generateContent(prompt)
                    response.text ?: ""
                }

                if (responseText.isNotBlank()) {
                    chatHistory.add("USER QUESTION:\n$question")
                    chatHistory.add("AI ANSWER:\n$responseText")
                    onResult(responseText)
                } else {
                    onResult("No response received. Please try again.")
                }

            } catch (e: Exception) {
                Log.e("DOUBT_ERROR", "Ask Doubt Failed", e)
                onResult("Error: ${e.localizedMessage ?: "Something went wrong"}")
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }

    fun deleteHistoryItem(id: Int) {
        viewModelScope.launch {
            dao.deleteById(id)
        }
    }

    fun insertHistoryItem(item: StudyHistoryEntity) {
        viewModelScope.launch {
            dao.insertHistory(item)
        }
    }

    private fun safeExtract(text: String, start: String, end: String): String {
        val extracted = text.substringAfter(start, "").trim()

        return if (end.isBlank()) {
            extracted
        } else {
            extracted.substringBefore(end, "").trim()
        }
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

            val explanationLine = lines.find { it.startsWith("Explanation:") } ?: ""
            val explanation = explanationLine.substringAfter("Explanation:").trim()

            if (question.isNotBlank() && options.size >= 4) {
                mcqList.add(
                    Mcq(
                        question = question,
                        options = options,
                        answer = answer.ifBlank { "Not given" },
                        explanation = explanation.ifBlank { "No explanation given." }
                    )
                )
            }
        }

        return mcqList
    }

    // UPDATED: refreshMcqs now supports difficulty
    fun refreshMcqs(input: String, difficulty: String) {

        lastInputText = input
        lastMcqDifficulty = difficulty

        viewModelScope.launch {
            try {
                val limitedInput = input.take(8000)

                val difficultyRule = when (difficulty.lowercase()) {
                    "easy" -> "Make MCQs very easy. Use direct questions."
                    "hard" -> "Make MCQs hard. Use tricky conceptual questions."
                    else -> "Make MCQs medium level. Balance easy and conceptual questions."
                }

                val prompt = """
            You are an expert teacher.
            
            Student Education Level / Grade: "$lastLevelText"
            
            MCQ Difficulty: "$difficulty"
            $difficultyRule
            
            Generate ONLY 10 new MCQs from the below content.
            
            VERY IMPORTANT RULES:
            - Do not repeat previous questions.
            - Use simple English.
            - Do not use markdown.
            - Output must follow exact format.
            
            Content:
            "$limitedInput"
            
            Return output ONLY in this exact format:
            
            [MCQS]
            1) Question
            A) option
            B) option
            C) option
            D) option
            Answer: A
            Explanation: short easy explanation
            
            (repeat 10 mcqs)
            """.trimIndent()

                val responseText = withContext(Dispatchers.IO) {
                    val response = generativeModel.generateContent(prompt)
                    response.text ?: ""
                }

                val mcqRaw = safeExtract(responseText, "[MCQS]", "")
                val newMcqs = parseMcqs(mcqRaw)

                val currentState = _uiState.value
                if (currentState is UiState.Success) {
                    val updatedNotes = currentState.notes.copy(
                        mcqs = newMcqs,
                        mcqsRaw = mcqRaw
                    )
                    _uiState.value = UiState.Success(updatedNotes)
                }

            } catch (e: Exception) {
                Log.e("MCQ_REFRESH_ERROR", "Refresh MCQs Failed", e)
            }
        }
    }

    private fun parseNotes(text: String): StudyNotes {
        val mcqRaw = safeExtract(text, "[MCQS]", "[SUMMARY]")

        return StudyNotes(
            shortNotes = safeExtract(text, "[SHORT NOTES]", "[QUESTIONS]"),
            importantQuestions = safeExtract(text, "[QUESTIONS]", "[MCQS]"),
            mcqs = parseMcqs(mcqRaw),
            summary = text.substringAfter("[SUMMARY]", "").trim(),
            mcqsRaw = mcqRaw
        )
    }
    fun loadHistoryNotes(notes: StudyNotes) {
        _uiState.value = UiState.Success(notes)
    }

    fun isInternetAvailable(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager

        val network = connectivityManager.activeNetwork
            ?: return false

        val capabilities =
            connectivityManager.getNetworkCapabilities(network)
                ?: return false

        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }

    fun saveQuizResult(
        title: String,
        score: Int,
        totalQuestions: Int,
        quizJson: String
    ) {

        viewModelScope.launch {

            quizHistoryDao.insertQuiz(
                QuizHistoryEntity(
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

            quizHistoryDao.deleteQuiz(id)
        }
    }

    fun clearQuizHistory() {

        viewModelScope.launch {

            quizHistoryDao.clearQuizHistory()
        }
    }

    fun saveQuiz(quiz: QuizHistoryEntity) {

        viewModelScope.launch {

            quizHistoryDao.insertQuiz(quiz)
        }
    }

    fun getQuizById(id: Int): Flow<QuizHistoryEntity?> {
        return quizHistoryDao.getQuizById(id)
    }
}