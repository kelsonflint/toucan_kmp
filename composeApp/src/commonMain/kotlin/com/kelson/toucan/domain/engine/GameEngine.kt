package com.kelson.toucan.domain.engine

import com.kelson.toucan.domain.models.*
import kotlin.random.Random

class GameEngine(
    private val deck: PromptDeck,
    private val players: List<String>,
    private val promptsPerGame: Int = 30
) {
    private val gamePrompts: MutableList<Prompt>
    private var currentPromptIndex = 0
    private var promptsSinceVirus = 0

    private var activeVirusCureText: String? = null
    private var virusPromptsRemaining = 0

    init {
        require(players.size >= 2) { "At least 2 players required" }
        require(deck.prompts.isNotEmpty()) { "Deck must have at least one prompt" }

        // Filter prompts that require more players than we have
        val eligiblePrompts = deck.prompts.filter { it.numTargets <= players.size }
        require(eligiblePrompts.isNotEmpty()) { "No prompts available for ${players.size} players" }

        // Shuffle and take a random subset for this game
        gamePrompts = eligiblePrompts
            .shuffled()
            .take(promptsPerGame)
            .toMutableList()
    }

    fun startGame(): PromptResult {
        currentPromptIndex = 0
        promptsSinceVirus = 0
        activeVirusCureText = null
        virusPromptsRemaining = 0

        return drawNextPrompt()!!
    }

    fun nextPrompt(): PromptResult? {
        if (virusPromptsRemaining > 0) {
            virusPromptsRemaining--
            if (virusPromptsRemaining == 0) {
                val cureResult = PromptResult(
                    text = activeVirusCureText!!,
                    type = PromptType.Virus,
                    isVirusCure = true,
                    hasActiveVirus = false
                )
                activeVirusCureText = null
                return cureResult
            }
        }

        return drawNextPrompt()
    }

    private fun drawNextPrompt(): PromptResult? {
        if (currentPromptIndex >= gamePrompts.size) {
            return null
        }

        val prompt = gamePrompts[currentPromptIndex]
        currentPromptIndex++

        // For virus prompts, use the same player order for both primary and secondary text
        val targetPlayers = if (prompt.numTargets > 0) players.shuffled().take(prompt.numTargets) else emptyList()
        val interpolatedText = interpolateWithPlayers(prompt.text, targetPlayers)

        if (prompt is VirusPrompt) {
            activeVirusCureText = interpolateWithPlayers(prompt.secondary, targetPlayers)
            virusPromptsRemaining = Random.nextInt(3, 6)
        }

        return PromptResult(
            text = interpolatedText,
            type = prompt.type,
            isVirusCure = false,
            hasActiveVirus = activeVirusCureText != null
        )
    }

    private fun interpolateWithPlayers(text: String, targetPlayers: List<String>): String {
        var result = text
        for (player in targetPlayers) {
            if (!result.contains("%s")) break
            result = result.replaceFirst("%s", player)
        }
        return result
    }

    fun isGameOver(): Boolean {
        return currentPromptIndex >= gamePrompts.size && virusPromptsRemaining == 0
    }
}
