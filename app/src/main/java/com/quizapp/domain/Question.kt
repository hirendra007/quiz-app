package com.quizapp.domain

/**
 * Domain model representing a quiz question
 */
data class Question(
    val id: Int,
    val topic: String,
    val text: String,
    val options: List<String>,
    val correctIndex: Int
) {
    init {
        require(options.size == 4) { "Question must have exactly 4 options" }
        require(correctIndex in 0..3) { "Correct index must be between 0 and 3" }
    }
}
