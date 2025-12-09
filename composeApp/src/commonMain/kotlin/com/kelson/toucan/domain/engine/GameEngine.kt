package com.kelson.toucan.domain.engine

import com.kelson.toucan.domain.models.*
import kotlin.random.Random

class GameEngine(
    private val deck: PromptDeck,
    private val players: List<String>
) {
    private val shuffledPrompts = deck.prompts.toMutableList().apply { shuffle() }
    private var currentPromptIndex = 0
    private var promptsSinceVirus = 0

    private var activeVirus: VirusPrompt? = null
    private var virusPromptsRemaining = 0

    init {
        require(players.size >= 2) { "At least 2 players required" }
        require(deck.prompts.isNotEmpty()) { "Deck must have at least one prompt" }
    }

    fun startGame(): PromptResult {
        currentPromptIndex = 0
        promptsSinceVirus = 0
        activeVirus = null
        virusPromptsRemaining = 0
        shuffledPrompts.shuffle()

        return drawNextPrompt()!!
    }

    fun nextPrompt(): PromptResult? {
        if (virusPromptsRemaining > 0) {
            virusPromptsRemaining--
            if (virusPromptsRemaining == 0) {
                val cureResult = PromptResult(
                    text = activeVirus!!.secondary,
                    type = PromptType.Virus,
                    isVirusCure = true,
                    hasActiveVirus = false
                )
                activeVirus = null
                return cureResult
            }
        }

        return drawNextPrompt()
    }

    private fun drawNextPrompt(): PromptResult? {
        if (currentPromptIndex >= shuffledPrompts.size) {
            return null
        }

        val prompt = shuffledPrompts[currentPromptIndex]
        currentPromptIndex++

        val interpolatedText = interpolatePrompt(prompt)

        if (prompt is VirusPrompt) {
            activeVirus = prompt
            virusPromptsRemaining = Random.nextInt(3, 6)
        }

        return PromptResult(
            text = interpolatedText,
            type = prompt.type,
            isVirusCure = false,
            hasActiveVirus = activeVirus != null
        )
    }

    private fun interpolatePrompt(prompt: Prompt): String {
        if (prompt.numTargets == 0) {
            return prompt.text
        }

        val shuffledPlayers = players.shuffled()
        var result = prompt.text

        for (player in shuffledPlayers) {
            if (!result.contains("%s")) break
            result = result.replaceFirst("%s", player)
        }

        return result
    }

    fun isGameOver(): Boolean {
        return currentPromptIndex >= shuffledPrompts.size && virusPromptsRemaining == 0
    }
}
