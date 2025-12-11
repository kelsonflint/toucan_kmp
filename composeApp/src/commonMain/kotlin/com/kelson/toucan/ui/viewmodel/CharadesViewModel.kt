package com.kelson.toucan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelson.toucan.data.DeckRepository
import com.kelson.toucan.domain.models.CharadesPrompt
import com.kelson.toucan.domain.models.CharadesRoundResult
import com.kelson.toucan.domain.models.GameMode
import com.kelson.toucan.domain.sensor.TiltDirection
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CharadesUiState {
    data object Loading : CharadesUiState()
    data object GetReady : CharadesUiState()
    data class Playing(
        val currentPrompt: String,
        val timeRemaining: Int,
        val promptIndex: Int
    ) : CharadesUiState()
    data class Correct(val prompt: String) : CharadesUiState()
    data class Skipped(val prompt: String) : CharadesUiState()
    data class RoundOver(val result: CharadesRoundResult) : CharadesUiState()
    data class Error(val message: String) : CharadesUiState()
}

class CharadesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<CharadesUiState>(CharadesUiState.Loading)
    val uiState: StateFlow<CharadesUiState> = _uiState.asStateFlow()

    private val repository = DeckRepository()
    private var prompts: MutableList<String> = mutableListOf()
    private var currentPromptIndex = 0
    private var roundResults: MutableList<CharadesPrompt> = mutableListOf()
    private var timerJob: Job? = null
    private var timeRemaining = ROUND_DURATION_SECONDS
    private var isProcessingTilt = false

    companion object {
        const val ROUND_DURATION_SECONDS = 60
        const val FEEDBACK_DELAY_MS = 800L
        const val GET_READY_DELAY_MS = 3000L
    }

    fun initialize(deckId: String) {
        _uiState.value = CharadesUiState.Loading
        currentPromptIndex = 0
        roundResults.clear()
        timeRemaining = ROUND_DURATION_SECONDS

        viewModelScope.launch {
            try {
                val deck = repository.loadDeck(GameMode.ToucanCharades, deckId)
                prompts = deck.prompts.map { it.text }.shuffled().toMutableList()

                // Show "Get Ready" screen
                _uiState.value = CharadesUiState.GetReady
            } catch (e: Exception) {
                _uiState.value = CharadesUiState.Error("Failed to load deck: ${e.message}")
            }
        }
    }

    fun startRound() {
        if (prompts.isEmpty()) return

        currentPromptIndex = 0
        roundResults.clear()
        timeRemaining = ROUND_DURATION_SECONDS
        isProcessingTilt = false

        showCurrentPrompt()
        startTimer()
    }

    private fun showCurrentPrompt() {
        if (currentPromptIndex < prompts.size && timeRemaining > 0) {
            _uiState.value = CharadesUiState.Playing(
                currentPrompt = prompts[currentPromptIndex],
                timeRemaining = timeRemaining,
                promptIndex = currentPromptIndex
            )
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (timeRemaining > 0) {
                delay(1000)
                timeRemaining--

                val currentState = _uiState.value
                if (currentState is CharadesUiState.Playing) {
                    _uiState.value = currentState.copy(timeRemaining = timeRemaining)
                }
            }
            endRound()
        }
    }

    fun onTiltDetected(direction: TiltDirection) {
        // Ignore if not in playing state or already processing
        val currentState = _uiState.value
        if (currentState !is CharadesUiState.Playing || isProcessingTilt) return

        when (direction) {
            TiltDirection.TILTED_DOWN -> markCorrect()
            TiltDirection.TILTED_UP -> markSkipped()
            TiltDirection.NEUTRAL -> { /* Do nothing */ }
        }
    }

    private fun markCorrect() {
        if (isProcessingTilt) return
        isProcessingTilt = true

        val prompt = prompts.getOrNull(currentPromptIndex) ?: return
        roundResults.add(CharadesPrompt(prompt, wasCorrect = true))

        _uiState.value = CharadesUiState.Correct(prompt)

        viewModelScope.launch {
            delay(FEEDBACK_DELAY_MS)
            moveToNextPrompt()
        }
    }

    private fun markSkipped() {
        if (isProcessingTilt) return
        isProcessingTilt = true

        val prompt = prompts.getOrNull(currentPromptIndex) ?: return
        roundResults.add(CharadesPrompt(prompt, wasCorrect = false))

        _uiState.value = CharadesUiState.Skipped(prompt)

        viewModelScope.launch {
            delay(FEEDBACK_DELAY_MS)
            moveToNextPrompt()
        }
    }

    private fun moveToNextPrompt() {
        isProcessingTilt = false
        currentPromptIndex++

        if (currentPromptIndex >= prompts.size || timeRemaining <= 0) {
            endRound()
        } else {
            showCurrentPrompt()
        }
    }

    private fun endRound() {
        timerJob?.cancel()


        val result = CharadesRoundResult(
            prompts = roundResults.toList(),
            correctCount = roundResults.count { it.wasCorrect == true },
            skippedCount = roundResults.count { it.wasCorrect == false },
            totalAttempted = roundResults.size
        )

        _uiState.value = CharadesUiState.RoundOver(result)
    }

    fun playAgain() {
        // Reshuffle and start new round
        prompts.shuffle()
        startRound()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
