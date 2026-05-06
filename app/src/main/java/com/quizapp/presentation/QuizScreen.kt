package com.quizapp.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun QuizScreen(
    state: QuizUiState.Quiz,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress Section
        QuizProgressSection(
            currentQuestion = state.currentQuestionIndex + 1,
            totalQuestions = state.questions.size,
            progress = state.progress,
            timeRemaining = state.timeRemaining
        )

        // Topic Badge
        TopicBadge(topic = state.currentQuestion.topic)

        // Question Card
        QuestionCard(
            questionText = state.currentQuestion.text,
            questionNumber = state.currentQuestionIndex + 1
        )

        // Answer Options
        AnswerOptions(
            options = state.currentQuestion.options,
            selectedIndex = state.selectedAnswerIndex,
            correctIndex = state.currentQuestion.correctIndex,
            isAnswerSubmitted = state.isAnswerSubmitted,
            onAnswerSelected = onAnswerSelected
        )

        Spacer(modifier = Modifier.weight(1f))

        // Score Display
        ScoreDisplay(
            score = state.score,
            totalQuestions = state.questions.size
        )
    }
}

@Composable
private fun QuizProgressSection(
    currentQuestion: Int,
    totalQuestions: Int,
    progress: Float,
    timeRemaining: Int
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question $currentQuestion of $totalQuestions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            TimerChip(timeRemaining = timeRemaining)
        }

        // Animated Progress Bar
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(durationMillis = 300),
            label = "progress"
        )

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun TimerChip(timeRemaining: Int) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            timeRemaining > 20 -> MaterialTheme.colorScheme.primaryContainer
            timeRemaining > 10 -> MaterialTheme.colorScheme.tertiaryContainer
            else -> MaterialTheme.colorScheme.errorContainer
        },
        label = "timer_background"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            timeRemaining > 20 -> MaterialTheme.colorScheme.onPrimaryContainer
            timeRemaining > 10 -> MaterialTheme.colorScheme.onTertiaryContainer
            else -> MaterialTheme.colorScheme.onErrorContainer
        },
        label = "timer_content"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = "${timeRemaining}s",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
private fun TopicBadge(topic: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = topic,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun QuestionCard(
    questionText: String,
    questionNumber: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Q$questionNumber",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = questionText,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AnswerOptions(
    options: List<String>,
    selectedIndex: Int?,
    correctIndex: Int,
    isAnswerSubmitted: Boolean,
    onAnswerSelected: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEachIndexed { index, option ->
            AnswerOptionButton(
                text = option,
                index = index,
                isSelected = selectedIndex == index,
                isCorrect = index == correctIndex,
                isAnswerSubmitted = isAnswerSubmitted,
                onClick = { onAnswerSelected(index) }
            )
        }
    }
}

@Composable
private fun AnswerOptionButton(
    text: String,
    index: Int,
    isSelected: Boolean,
    isCorrect: Boolean,
    isAnswerSubmitted: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            !isAnswerSubmitted -> MaterialTheme.colorScheme.surface
            isSelected && isCorrect -> Color(0xFF4CAF50) // Green
            isSelected && !isCorrect -> Color(0xFFF44336) // Red
            !isSelected && isCorrect -> Color(0xFF4CAF50) // Show correct answer
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(durationMillis = 300),
        label = "answer_background"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            !isAnswerSubmitted -> MaterialTheme.colorScheme.onSurface
            isSelected || (!isSelected && isCorrect && isAnswerSubmitted) -> Color.White
            else -> MaterialTheme.colorScheme.onSurface
        },
        label = "answer_content"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            !isAnswerSubmitted -> MaterialTheme.colorScheme.outline
            isSelected && isCorrect -> Color(0xFF4CAF50)
            isSelected && !isCorrect -> Color(0xFFF44336)
            !isSelected && isCorrect -> Color(0xFF4CAF50)
            else -> MaterialTheme.colorScheme.outline
        },
        label = "answer_border"
    )

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        enabled = !isAnswerSubmitted,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = backgroundColor,
            disabledContentColor = contentColor
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(borderColor)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${'A' + index}.",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = text,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun ScoreDisplay(
    score: Int,
    totalQuestions: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Current Score",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "$score / $totalQuestions",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
