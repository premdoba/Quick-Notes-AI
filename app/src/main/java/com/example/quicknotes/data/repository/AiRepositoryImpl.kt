package com.example.quicknotes.data.repository

import com.example.quicknotes.BuildConfig
import com.example.quicknotes.domain.model.Mcq
import com.example.quicknotes.domain.model.StudyNotes
import com.example.quicknotes.domain.repository.AiRepository
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiRepositoryImpl : AiRepository {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.API_KEY
    )

    override suspend fun generateStudyMaterial(input: String, educationLevel: String): StudyNotes? {
        return withContext(Dispatchers.IO) {
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

                val response = generativeModel.generateContent(prompt)
                val responseText = response.text ?: ""
                if (responseText.isBlank()) null else parseNotes(responseText)
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun askDoubt(question: String, educationLevel: String, contextText: String, historyText: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                You are a helpful teacher.
                Student Education Level: "$educationLevel"
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

                val response = generativeModel.generateContent(prompt)
                response.text ?: ""
            } catch (e: Exception) {
                "Error: ${e.localizedMessage ?: "Something went wrong"}"
            }
        }
    }

    override suspend fun generateMcqs(input: String, educationLevel: String, difficulty: String): Pair<List<Mcq>, String>? {
        return withContext(Dispatchers.IO) {
            try {
                val limitedInput = input.take(8000)
                val difficultyRule = when (difficulty.lowercase()) {
                    "easy" -> "Make MCQs very easy. Use direct questions."
                    "hard" -> "Make MCQs hard. Use tricky conceptual questions."
                    else -> "Make MCQs medium level. Balance easy and conceptual questions."
                }
                val prompt = """
                You are an expert teacher.
                Student Education Level / Grade: "$educationLevel"
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

                val response = generativeModel.generateContent(prompt)
                val responseText = response.text ?: ""
                val mcqRaw = safeExtract(responseText, "[MCQS]", "")
                if (mcqRaw.isBlank()) null else Pair(parseMcqs(mcqRaw), mcqRaw)
            } catch (e: Exception) {
                null
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

    private fun parseMcqs(mcqText: String): List<Mcq> {
        val blocks = mcqText.split(Regex("""\n(?=\d+\))"""))
        val mcqList = mutableListOf<Mcq>()
        for (block in blocks) {
            val lines = block.lines().map { it.trim() }.filter { it.isNotBlank() }
            if (lines.size < 6) continue
            val questionLine = lines.firstOrNull { it.contains(")") } ?: continue
            val question = questionLine.substringAfter(")").trim()
            val options = lines.filter { it.startsWith("A)") || it.startsWith("B)") || it.startsWith("C)") || it.startsWith("D)") }
            val answerLine = lines.find { it.startsWith("Answer:") } ?: ""
            val answer = answerLine.substringAfter("Answer:").trim()
            val explanationLine = lines.find { it.startsWith("Explanation:") } ?: ""
            val explanation = explanationLine.substringAfter("Explanation:").trim()
            if (question.isNotBlank() && options.size >= 4) {
                mcqList.add(Mcq(question, options, answer.ifBlank { "Not given" }, explanation.ifBlank { "No explanation given." }))
            }
        }
        return mcqList
    }

    private fun safeExtract(text: String, start: String, end: String): String {
        val extracted = text.substringAfter(start, "").trim()
        return if (end.isBlank()) extracted else extracted.substringBefore(end, "").trim()
    }
}