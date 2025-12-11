package com.kelson.toucan.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.kelson.toucan.ui.utils.isLandscape
import org.jetbrains.compose.resources.painterResource
import toucan.composeapp.generated.resources.Res
import toucan.composeapp.generated.resources.big_toucan
import toucan.composeapp.generated.resources.toucan_flipped

@Composable
fun PlayerSetupScreen(
    onContinue: (List<String>) -> Unit,
    onBack: () -> Unit
) {
    var playerName by remember { mutableStateOf("") }
    var players by remember { mutableStateOf(listOf<String>()) }
    var showError by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    fun addPlayer() {
        val trimmedName = playerName.trim()
        if (trimmedName.isNotEmpty() && !players.contains(trimmedName)) {
            players = players + trimmedName
            playerName = ""
            showError = false
        }
    }

    fun dismissKeyboard() {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { dismissKeyboard() }
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!isLandscape()) {
                    Image(painterResource(Res.drawable.big_toucan), null, modifier = Modifier.size(128.dp))
                    Image(painterResource(Res.drawable.toucan_flipped), null, modifier = Modifier.size(96.dp))
                }
            }

            Text(
                text = "Add Players",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "At least 2 players required",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = playerName,
                    onValueChange = { playerName = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Player name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        addPlayer()
                        dismissKeyboard()
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                FilledIconButton(
                    onClick = { addPlayer() },
                    enabled = playerName.isNotBlank()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add player")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Players (${players.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            players.forEachIndexed { index, player ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}.",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = player,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = { players = players.filterIndexed { i, _ -> i != index } }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove player",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            if (showError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Add at least 2 players to continue",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }

            Button(
                onClick = {
                    if (players.size >= 2) {
                        onContinue(players)
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = players.size >= 2
            ) {
                Text("Continue")
            }
        }
    }
}
