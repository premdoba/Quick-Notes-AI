package com.example.quicknotes.domain.model

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