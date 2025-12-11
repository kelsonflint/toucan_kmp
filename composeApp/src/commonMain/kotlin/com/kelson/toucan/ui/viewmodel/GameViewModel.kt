package com.kelson.toucan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelson.toucan.data.DeckRepository
import com.kelson.toucan.domain.engine.GameEngine
import com.kelson.toucan.domain.models.GameMode
import com.kelson.toucan.domain.models.PromptDeck
import com.kelson.toucan.domain.models.PromptType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class GameUiState {
    data object Loading : GameUiState()
    data class Active(
        val promptText: String,
        val promptType: PromptType,
        val isVirusCure: Boolean = false,
        val hasActiveVirus: Boolean = false
    ) : GameUiState()
    data object GameOver : GameUiState()
    data class Error(val message: String) : GameUiState()
}

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val repository = DeckRepository()
    private var gameEngine: GameEngine? = null
    private var currentDeck: PromptDeck? = null
    private var currentPlayers: List<String> = emptyList()
    private var currentGameMode: GameMode = GameMode.ToucanDrink

    fun initialize(gameMode: GameMode, deckId: String, players: List<String>) {
        currentPlayers = players
        currentGameMode = gameMode
        _uiState.value = GameUiState.Loading

        viewModelScope.launch {
            try {
                val deck = repository.loadDeck(gameMode, deckId)
                currentDeck = deck
                gameEngine = GameEngine(deck, players)
                startGame()
            } catch (e: Exception) {
                _uiState.value = GameUiState.Error("Failed to load deck: ${e.message}")
            }
        }
    }

    private fun startGame() {
        val engine = gameEngine ?: return

        try {
            val result = engine.startGame()
            _uiState.value = GameUiState.Active(
                promptText = result.text,
                promptType = result.type,
                isVirusCure = result.isVirusCure,
                hasActiveVirus = result.hasActiveVirus
            )
        } catch (e: Exception) {
            _uiState.value = GameUiState.Error("Failed to start game: ${e.message}")
        }
    }

    fun nextPrompt() {
        val engine = gameEngine ?: return

        val result = engine.nextPrompt()
        if (result == null) {
            _uiState.value = GameUiState.GameOver
        } else {
            _uiState.value = GameUiState.Active(
                promptText = result.text,
                promptType = result.type,
                isVirusCure = result.isVirusCure,
                hasActiveVirus = result.hasActiveVirus
            )
        }
    }

    fun resetGame() {
        val deck = currentDeck ?: return
        gameEngine = GameEngine(deck, currentPlayers)
        startGame()
    }
}
