package com.kelson.toucan.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kelson.toucan.data.DeckRepository
import com.kelson.toucan.domain.models.DeckInfo
import com.kelson.toucan.ui.utils.isLandscape
import org.jetbrains.compose.resources.painterResource
import toucan.composeapp.generated.resources.Res
import toucan.composeapp.generated.resources.deck_art
import toucan.composeapp.generated.resources.toucan_flipped

@Composable
fun DeckSelectionScreen(
    players: List<String>,
    onSelectDeck: (String) -> Unit,
    onBack: () -> Unit
) {
    val repository = remember { DeckRepository() }
    val decks = remember { repository.getAvailableDecks() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                    Image(
                        painterResource(Res.drawable.deck_art),
                        null,
                        modifier = Modifier.size(128.dp)
                    )
                    Image(
                        painterResource(Res.drawable.toucan_flipped),
                        null,
                        modifier = Modifier.size(128.dp)
                    )
                }
            }

            Text(
                text = "Choose a Deck",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Playing with ${players.size} players",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            decks.forEach { deck ->
                DeckCard(
                    deck = deck,
                    onClick = { onSelectDeck(deck.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

@Composable
private fun DeckCard(
    deck: DeckInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = deck.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "${deck.promptCount} prompts",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = deck.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Deck")
            }
        }
    }
}
