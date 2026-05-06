package com.quizapp.presentation

import com.quizapp.domain.Question
import com.quizapp.domain.QuizResult
import com.quizapp.domain.TopicPerformance

/**
 * Sealed class representing different UI states
 */
sealed class QuizUiState {
    object Loading : QuizUiState()
    data class Quiz(
        val questions: List<Question>,
        val currentQuestionIndex: Int,
        val selectedAnswerIndex: Int?,
        val isAnswerSubmitted: Boolean,
        val score: Int,
        val timeRemaining: Int,
        val results: List<QuizResult>
    ) : QuizUiState() {
        val currentQuestion: Question
            get() = questions[currentQuestionIndex]
        
        val progress: Float
            get() = (currentQuestionIndex + 1).toFloat() / questions.size
        
        val isLastQuestion: Boolean
            get() = currentQuestionIndex == questions.size - 1
    }
    
    data class Result(
        val score: Int,
        val totalQuestions: Int,
        val results: List<QuizResult>,
        val topicPerformance: List<TopicPerformance>
    ) : QuizUiState() {
        val percentage: Float
            get() = (score.toFloat() / totalQuestions) * 100
        
        val performanceLabel: String
            get() = when {
                percentage >= 90 -> "Outstanding! 🏆"
                percentage >= 75 -> "Excellent! 🌟"
                percentage >= 60 -> "Good Job! 👍"
                percentage >= 40 -> "Keep Practicing! 📚"
                else -> "Need More Study! 💪"
            }
    }
}

/**
 * Sealed class for one-time events
 */
sealed class QuizEvent {
    object NavigateToResults : QuizEvent()
    data class ShowToast(val message: String) : QuizEvent()
}
