package com.quizapp.domain

/**
 * Represents the result of a single answered question
 */
data class QuizResult(
    val question: Question,
    val selectedIndex: Int?,
    val isCorrect: Boolean,
    val timeTaken: Int // seconds
)

/**
 * Represents topic-wise performance
 */
data class TopicPerformance(
    val topic: String,
    val correct: Int,
    val total: Int
) {
    val percentage: Float
        get() = if (total > 0) (correct.toFloat() / total) * 100 else 0f
}
