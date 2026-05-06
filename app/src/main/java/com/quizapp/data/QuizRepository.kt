package com.quizapp.data

import com.quizapp.domain.Question

/**
 * Repository interface for quiz data
 */
interface QuizRepository {
    fun getQuestions(): List<Question>
}

/**
 * Implementation of QuizRepository with hardcoded questions
 * In production, this would fetch from API or local database
 */
class QuizRepositoryImpl : QuizRepository {
    
    override fun getQuestions(): List<Question> {
        return listOf(
            // Kotlin Questions (5)
            Question(
                id = 1,
                topic = "Kotlin",
                text = "What is the correct way to declare a nullable variable in Kotlin?",
                options = listOf(
                    "var name: String",
                    "var name: String?",
                    "var name: String!",
                    "var name?: String"
                ),
                correctIndex = 1
            ),
            Question(
                id = 2,
                topic = "Kotlin",
                text = "Which keyword is used to create a singleton in Kotlin?",
                options = listOf(
                    "singleton",
                    "static",
                    "object",
                    "companion"
                ),
                correctIndex = 2
            ),
            Question(
                id = 3,
                topic = "Kotlin",
                text = "What does the 'lateinit' keyword do?",
                options = listOf(
                    "Makes a variable immutable",
                    "Allows non-null var to be initialized later",
                    "Creates a lazy property",
                    "Declares a constant"
                ),
                correctIndex = 1
            ),
            Question(
                id = 4,
                topic = "Kotlin",
                text = "Which function is used to apply multiple operations on an object?",
                options = listOf(
                    "let",
                    "run",
                    "apply",
                    "with"
                ),
                correctIndex = 2
            ),
            Question(
                id = 5,
                topic = "Kotlin",
                text = "What is the difference between 'val' and 'var'?",
                options = listOf(
                    "val is for integers, var is for strings",
                    "val is mutable, var is immutable",
                    "val is immutable, var is mutable",
                    "There is no difference"
                ),
                correctIndex = 2
            ),
            
            // Android Questions (5)
            Question(
                id = 6,
                topic = "Android",
                text = "Which component is used to perform background operations in Android?",
                options = listOf(
                    "Activity",
                    "Service",
                    "Fragment",
                    "BroadcastReceiver"
                ),
                correctIndex = 1
            ),
            Question(
                id = 7,
                topic = "Android",
                text = "What is the purpose of ViewModel in Android?",
                options = listOf(
                    "To handle database operations",
                    "To manage UI-related data in lifecycle-conscious way",
                    "To create custom views",
                    "To handle network requests"
                ),
                correctIndex = 1
            ),
            Question(
                id = 8,
                topic = "Android",
                text = "Which method is called when an Activity is first created?",
                options = listOf(
                    "onStart()",
                    "onResume()",
                    "onCreate()",
                    "onInit()"
                ),
                correctIndex = 2
            ),
            Question(
                id = 9,
                topic = "Android",
                text = "What is the minimum SDK version for Jetpack Compose?",
                options = listOf(
                    "API 19",
                    "API 21",
                    "API 23",
                    "API 26"
                ),
                correctIndex = 1
            ),
            Question(
                id = 10,
                topic = "Android",
                text = "Which architecture pattern is recommended by Google for Android apps?",
                options = listOf(
                    "MVC",
                    "MVP",
                    "MVVM",
                    "VIPER"
                ),
                correctIndex = 2
            ),
            
            // Jetpack Compose Questions (5)
            Question(
                id = 11,
                topic = "Jetpack Compose",
                text = "What annotation is used to mark a Composable function?",
                options = listOf(
                    "@Compose",
                    "@Composable",
                    "@UI",
                    "@View"
                ),
                correctIndex = 1
            ),
            Question(
                id = 12,
                topic = "Jetpack Compose",
                text = "Which function is used to remember state across recompositions?",
                options = listOf(
                    "useState()",
                    "rememberState()",
                    "remember { }",
                    "mutableState()"
                ),
                correctIndex = 2
            ),
            Question(
                id = 13,
                topic = "Jetpack Compose",
                text = "What is recomposition in Jetpack Compose?",
                options = listOf(
                    "Creating a new Composable",
                    "Re-executing Composables when state changes",
                    "Destroying a Composable",
                    "Navigating between screens"
                ),
                correctIndex = 1
            ),
            Question(
                id = 14,
                topic = "Jetpack Compose",
                text = "Which modifier is used to add padding in Compose?",
                options = listOf(
                    ".padding()",
                    ".margin()",
                    ".space()",
                    ".offset()"
                ),
                correctIndex = 0
            ),
            Question(
                id = 15,
                topic = "Jetpack Compose",
                text = "What is the purpose of LaunchedEffect in Compose?",
                options = listOf(
                    "To launch animations",
                    "To run suspend functions in Composable scope",
                    "To create side effects",
                    "To launch new activities"
                ),
                correctIndex = 1
            )
        )
    }
}
