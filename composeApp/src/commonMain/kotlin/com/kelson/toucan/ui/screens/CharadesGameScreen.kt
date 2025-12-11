package com.kelson.toucan.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelson.toucan.domain.haptics.Haptics
import com.kelson.toucan.domain.haptics.rememberHaptics
import com.kelson.toucan.domain.models.CharadesRoundResult
import com.kelson.toucan.domain.sensor.TiltDirection
import com.kelson.toucan.ui.viewmodel.CharadesUiState
import com.kelson.toucan.ui.viewmodel.CharadesViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun CharadesGameScreen(
    deckId: String,
    tiltDirectionFlow: Flow<TiltDirection>,
    onExit: () -> Unit,
    onPlayAgain: () -> Unit,
    onLockOrientation: () -> Unit = {},
    onUnlockOrientation: () -> Unit = {},
    viewModel: CharadesViewModel = viewModel { CharadesViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptics = rememberHaptics()

    // Initialize the game
    LaunchedEffect(deckId) {
        viewModel.initialize(deckId)
    }

    // Collect tilt events and forward to viewmodel
    LaunchedEffect(Unit) {
        tiltDirectionFlow.collect { direction ->
            viewModel.onTiltDetected(direction)
        }
    }

    // Trigger haptics on state changes and unlock orientation when round ends
    LaunchedEffect(uiState) {
        when (uiState) {
            is CharadesUiState.Correct -> haptics.success()
            is CharadesUiState.Skipped -> haptics.error()
            is CharadesUiState.RoundOver -> onUnlockOrientation()
            else -> { /* no haptic */ }
        }
    }

    when (val state = uiState) {
        is CharadesUiState.Loading -> LoadingContent()
        is CharadesUiState.GetReady -> GetReadyContent(onStart = {
            onLockOrientation()
            viewModel.startRound()
        })
        is CharadesUiState.Playing -> PlayingContent(
            prompt = state.currentPrompt,
            timeRemaining = state.timeRemaining
        )
        is CharadesUiState.Correct -> FeedbackContent(
            prompt = state.prompt,
            isCorrect = true
        )
        is CharadesUiState.Skipped -> FeedbackContent(
            prompt = state.prompt,
            isCorrect = false
        )
        is CharadesUiState.RoundOver -> RoundOverContent(
            result = state.result,
            onPlayAgain = {
                onLockOrientation()
                viewModel.playAgain()
            },
            onExit = onExit
        )
        is CharadesUiState.Error -> CharadesErrorContent(
            message = state.message,
            onExit = onExit
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2196F3)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White,
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
private fun GetReadyContent(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2196F3))
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "GET READY!",
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Hold the phone to your forehead",
            fontSize = 20.sp,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onStart,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF2196F3)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "START",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "⬇️ Tilt DOWN = Correct",
            fontSize = 18.sp,
            color = Color(0xFF4CAF50),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "⬆️ Tilt UP = Skip",
            fontSize = 18.sp,
            color = Color(0xFFFF5722),
            fontWeight = FontWeight.Bold
        )



    }
}

@Composable
private fun PlayingContent(
    prompt: String,
    timeRemaining: Int
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2196F3))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Timer in top corner
        Text(
            text = timeRemaining.toString(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = if (timeRemaining <= 10) Color(0xFFFF5722) else Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        // Main prompt - large and centered for forehead viewing
        Text(
            text = prompt.uppercase(),
            fontSize = 56.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 64.sp
        )

        // Instructions at bottom
        Text(
            text = "⬇️ DOWN = Got it!  •  ⬆️ UP = Pass",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun FeedbackContent(
    prompt: String,
    isCorrect: Boolean
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFFF5722),
        animationSpec = tween(200),
        label = "feedback_bg"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                contentDescription = if (isCorrect) "Correct" else "Skipped",
                tint = Color.White,
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = if (isCorrect) "CORRECT!" else "PASS",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Text(
                text = prompt,
                fontSize = 24.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RoundOverContent(
    result: CharadesRoundResult,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(24.dp)
    ) {
        // Score header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TIME'S UP!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = result.correctCount.toString(),
                fontSize = 72.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF4CAF50)
            )

            Text(
                text = "correct answers",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${result.skippedCount} skipped • ${result.totalAttempted} total",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Results list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(result.prompts) { index, prompt ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (prompt.wasCorrect == true)
                            Color(0xFF4CAF50).copy(alpha = 0.1f)
                        else
                            Color(0xFFFF5722).copy(alpha = 0.1f)
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
                            text = prompt.text,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Icon(
                            imageVector = if (prompt.wasCorrect == true)
                                Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (prompt.wasCorrect == true)
                                Color(0xFF4CAF50) else Color(0xFFFF5722)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onExit,
                modifier = Modifier.weight(1f)
            ) {
                Text("Exit")
            }

            Button(
                onClick = onPlayAgain,
                modifier = Modifier.weight(1f)
            ) {
                Text("Play Again")
            }
        }
    }
}

@Composable
private fun CharadesErrorContent(
    message: String,
    onExit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Oops!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(onClick = onExit) {
                Text("Go Back")
            }
        }
    }
}
