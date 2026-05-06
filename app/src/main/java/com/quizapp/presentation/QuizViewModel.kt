package com.quizapp.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizapp.data.QuizRepository
import com.quizapp.domain.Question
import com.quizapp.domain.QuizResult
import com.quizapp.domain.TopicPerformance
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel managing quiz state and business logic
 * Uses SavedStateHandle to survive configuration changes
 */
class QuizViewModel(
    private val repository: QuizRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TIMER_DURATION = 30
        private const val KEY_CURRENT_INDEX = "current_index"
        private const val KEY_SCORE = "score"
        private const val KEY_TIME_REMAINING = "time_remaining"
        private const val KEY_RESULTS = "results"
    }

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<QuizEvent>()
    val events = _events.asSharedFlow()

    private var timerJob: Job? = null
    private var questions: List<Question> = emptyList()
    private var results: MutableList<QuizResult> = mutableListOf()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            questions = repository.getQuestions().shuffled()
            
            // Restore state if available
            val savedIndex = savedStateHandle.get<Int>(KEY_CURRENT_INDEX) ?: 0
            val savedScore = savedStateHandle.get<Int>(KEY_SCORE) ?: 0
            val savedTime = savedStateHandle.get<Int>(KEY_TIME_REMAINING) ?: TIMER_DURATION
            
            _uiState.value = QuizUiState.Quiz(
                questions = questions,
                currentQuestionIndex = savedIndex,
                selectedAnswerIndex = null,
                isAnswerSubmitted = false,
                score = savedScore,
                timeRemaining = savedTime,
                results = results
            )
            
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val currentState = _uiState.value as? QuizUiState.Quiz ?: return@launch
            var timeRemaining = currentState.timeRemaining
            
            while (timeRemaining > 0) {
                delay(1000)
                timeRemaining--
                
                val state = _uiState.value as? QuizUiState.Quiz ?: return@launch
                _uiState.value = state.copy(timeRemaining = timeRemaining)
                savedStateHandle[KEY_TIME_REMAINING] = timeRemaining
                
                if (timeRemaining == 0) {
                    handleTimeOut()
                }
            }
        }
    }

    fun selectAnswer(index: Int) {
        val currentState = _uiState.value as? QuizUiState.Quiz ?: return
        
        // Prevent selection if already submitted
        if (currentState.isAnswerSubmitted) return
        
        timerJob?.cancel()
        
        val isCorrect = index == currentState.currentQuestion.correctIndex
        val newScore = if (isCorrect) currentState.score + 1 else currentState.score
        
        // Record result
        val timeTaken = TIMER_DURATION - currentState.timeRemaining
        val result = QuizResult(
            question = currentState.currentQuestion,
            selectedIndex = index,
            isCorrect = isCorrect,
            timeTaken = timeTaken
        )
        results.add(result)
        
        _uiState.value = currentState.copy(
            selectedAnswerIndex = index,
            isAnswerSubmitted = true,
            score = newScore
        )
        
        savedStateHandle[KEY_SCORE] = newScore
        
        // Auto-advance after delay
        viewModelScope.launch {
            delay(1500)
            moveToNextQuestion()
        }
    }

    private fun handleTimeOut() {
        val currentState = _uiState.value as? QuizUiState.Quiz ?: return
        
        // Record as unanswered
        val result = QuizResult(
            question = currentState.currentQuestion,
            selectedIndex = null,
            isCorrect = false,
            timeTaken = TIMER_DURATION
        )
        results.add(result)
        
        _uiState.value = currentState.copy(
            isAnswerSubmitted = true
        )
        
        viewModelScope.launch {
            delay(1500)
            moveToNextQuestion()
        }
    }

    private fun moveToNextQuestion() {
        val currentState = _uiState.value as? QuizUiState.Quiz ?: return
        
        if (currentState.isLastQuestion) {
            showResults()
        } else {
            val nextIndex = currentState.currentQuestionIndex + 1
            _uiState.value = currentState.copy(
                currentQuestionIndex = nextIndex,
                selectedAnswerIndex = null,
                isAnswerSubmitted = false,
                timeRemaining = TIMER_DURATION,
                results = results
            )
            
            savedStateHandle[KEY_CURRENT_INDEX] = nextIndex
            savedStateHandle[KEY_TIME_REMAINING] = TIMER_DURATION
            
            startTimer()
        }
    }

    private fun showResults() {
        timerJob?.cancel()
        
        val currentState = _uiState.value as? QuizUiState.Quiz ?: return
        
        // Calculate topic-wise performance
        val topicPerformance = calculateTopicPerformance()
        
        _uiState.value = QuizUiState.Result(
            score = currentState.score,
            totalQuestions = questions.size,
            results = results,
            topicPerformance = topicPerformance
        )
        
        // Clear saved state
        savedStateHandle.remove<Int>(KEY_CURRENT_INDEX)
        savedStateHandle.remove<Int>(KEY_SCORE)
        savedStateHandle.remove<Int>(KEY_TIME_REMAINING)
    }

    private fun calculateTopicPerformance(): List<TopicPerformance> {
        return results
            .groupBy { it.question.topic }
            .map { (topic, topicResults) ->
                TopicPerformance(
                    topic = topic,
                    correct = topicResults.count { it.isCorrect },
                    total = topicResults.size
                )
            }
            .sortedByDescending { it.percentage }
    }

    fun retryQuiz() {
        results.clear()
        savedStateHandle.remove<Int>(KEY_CURRENT_INDEX)
        savedStateHandle.remove<Int>(KEY_SCORE)
        savedStateHandle.remove<Int>(KEY_TIME_REMAINING)
        loadQuestions()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
