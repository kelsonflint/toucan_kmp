package com.kelson.toucan.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelson.toucan.domain.models.GameMode
import com.kelson.toucan.ui.theme.getPromptTypeColors
import com.kelson.toucan.ui.viewmodel.GameUiState
import com.kelson.toucan.ui.viewmodel.GameViewModel

@Composable
fun GameScreen(
    gameMode: GameMode,
    deckId: String,
    players: List<String>,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit,
    onSettings: () -> Unit = {},
    viewModel: GameViewModel = viewModel { GameViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(gameMode, deckId, players) {
        viewModel.initialize(gameMode, deckId, players)
    }

    when (val state = uiState) {
        is GameUiState.Loading -> LoadingContent()
        is GameUiState.Active -> ActiveGameContent(
            state = state,
            onTap = { viewModel.nextPrompt() },
            onExit = onExit,
            onSettings = onSettings
        )
        is GameUiState.GameOver -> GameOverContent(
            onPlayAgain = onPlayAgain,
            onExit = onExit
        )
        is GameUiState.Error -> ErrorContent(
            message = state.message,
            onRetry = { viewModel.initialize(gameMode, deckId, players) }
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ActiveGameContent(
    state: GameUiState.Active,
    onTap: () -> Unit,
    onExit: () -> Unit,
    onSettings: () -> Unit
) {
    val colors = getPromptTypeColors(state.promptType, state.isVirusCure)
    val animatedBackground by animateColorAsState(
        targetValue = colors.background,
        animationSpec = tween(durationMillis = 300),
        label = "background"
    )
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBackground)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onTap
            )
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section - Prompt type label + menu + virus indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = colors.label,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = colors.text.copy(alpha = 0.9f),
                    letterSpacing = 4.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.hasActiveVirus && !state.isVirusCure) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Virus active",
                            modifier = Modifier.size(32.dp),
                            tint = colors.text.copy(alpha = 0.8f)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                modifier = Modifier.size(32.dp),
                                tint = colors.text.copy(alpha = 0.8f)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = {
                                    showMenu = false
                                    onSettings()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Exit Game") },
                                onClick = {
                                    showMenu = false
                                    onExit()
                                }
                            )
                        }
                    }
                }
            }

            // Center section - Prompt text
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.promptText,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.text,
                    textAlign = TextAlign.Center,
                    lineHeight = 48.sp,
                    fontSize = 32.sp
                )
            }

            // Bottom section - Tap hint
            Text(
                text = "Tap anywhere to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.text.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun GameOverContent(
    onPlayAgain: () -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Text(
                text = "GAME OVER",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 4.sp
            )

            Text(
                text = "Thanks for playing!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Play Again",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = onExit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Exit to Home")
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
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

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Retry")
            }
        }
    }
}
