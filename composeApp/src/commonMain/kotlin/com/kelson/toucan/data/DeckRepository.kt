package com.kelson.toucan.data

import com.kelson.toucan.domain.models.*
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import toucan.composeapp.generated.resources.Res

class DeckRepository {

    private val json = Json { ignoreUnknownKeys = true }

    private val availableDecks = listOf(
        DeckInfo("getting_started", "Getting Started", "A well-rounded set of party prompts to get the fun going", 100),
        DeckInfo("bar_night", "Bar Night", "Perfect for a casual night out with friends at the bar", 97),
        DeckInfo("getting_crazy", "Getting Crazy", "Turn up the heat with daring challenges", 67),
        DeckInfo("ardente", "Ardente", "Fiery and spicy challenges for the bold", 97)
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
            "minigame", "game" -> MinigamePrompt(text = text, numTargets = numTargets)
            "virus" -> VirusPrompt(text = text, numTargets = numTargets, secondary = secondary ?: "The effect has ended!")
            "bottoms up" -> BottomsUpPrompt(text = text, numTargets = numTargets)
            "punishment" -> PunishmentPrompt(text = text, numTargets = numTargets)
            "ardente" -> ArdentePrompt(text = text, numTargets = numTargets)
            else -> NormalPrompt(text = text, numTargets = numTargets)
        }
    }
}
