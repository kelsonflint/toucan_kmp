package com.kelson.toucan.data

import com.kelson.toucan.domain.models.*
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import toucan.composeapp.generated.resources.Res

class DeckRepository {

    private val json = Json { ignoreUnknownKeys = true }

    private val availableDecks = listOf(
        DeckInfo("classic", "Classic", "A well-rounded set of party prompts", 20),
        DeckInfo("spicy", "Spicy", "Turn up the heat with daring challenges", 20),
        DeckInfo("minigames", "Minigames", "Fun mini-games and group activities", 15)
    )

    fun getAvailableDecks(): List<DeckInfo> = availableDecks

    @OptIn(ExperimentalResourceApi::class)
    suspend fun loadDeck(deckId: String): PromptDeck {
        val jsonString = Res.readBytes("files/decks/$deckId.json").decodeToString()
        val deckJson = json.decodeFromString<DeckJson>(jsonString)
        return deckJson.toDomain()
    }

    private fun DeckJson.toDomain(): PromptDeck {
        return PromptDeck(
            id = id,
            name = name,
            description = description,
            prompts = prompts.map { it.toDomain() }
        )
    }

    private fun PromptJson.toDomain(): Prompt {
        return when (type.lowercase()) {
            "normal" -> NormalPrompt(text = text, numTargets = numTargets)
            "minigame" -> MinigamePrompt(text = text, numTargets = numTargets)
            "virus" -> VirusPrompt(text = text, secondary = secondary ?: "The effect has ended!")
            else -> NormalPrompt(text = text, numTargets = numTargets)
        }
    }
}
