package com.kelson.toucan.data

import com.kelson.toucan.domain.models.*
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import toucan.composeapp.generated.resources.Res

class DeckRepository {

    private val json = Json { ignoreUnknownKeys = true }

    private val decksByMode = mapOf(
        GameMode.ToucanDrink to listOf(
            DeckInfo("getting_started", "Getting Started", "A well-rounded set of party prompts to get the fun going", 100),
            DeckInfo("bar_night", "Bar Night", "Perfect for a casual night out with friends at the bar", 97),
            DeckInfo("getting_crazy", "Getting Crazy", "Turn up the heat with daring challenges", 67),
            DeckInfo("ardente", "Ardente", "Fiery and spicy challenges for the bold", 97)
        ),
        GameMode.ItTakesTou to listOf(
            DeckInfo("getting_closer", "Getting Closer", "Light questions to warm up and get to know each other", 50),
            DeckInfo("deep_dive", "Deep Dive", "Thought-provoking questions for meaningful connection", 50),
            DeckInfo("heart_to_heart", "Heart to Heart", "Intimate questions for couples or close friends", 50)
        ),
        GameMode.ToucanDiscuss to listOf(
            DeckInfo("philosophy", "Philosophy", "Deep questions about life, existence, and meaning", 50),
            DeckInfo("would_you_rather", "Would You Rather", "Tough choices that spark debate", 50),
            DeckInfo("hot_takes", "Hot Takes", "Controversial opinions to discuss respectfully", 50)
        ),
        GameMode.ToucanCharades to listOf(
            DeckInfo("movies", "Movies", "Popular films to act out", 100),
            DeckInfo("celebrities", "Celebrities", "Famous people to impersonate", 100),
            DeckInfo("actions", "Actions", "Fun activities and scenarios", 100)
        )
    )

    fun getAvailableDecks(gameMode: GameMode): List<DeckInfo> =
        decksByMode[gameMode] ?: emptyList()

    @Deprecated("Use getAvailableDecks(gameMode) instead")
    fun getAvailableDecks(): List<DeckInfo> = getAvailableDecks(GameMode.ToucanDrink)

    @OptIn(ExperimentalResourceApi::class)
    suspend fun loadDeck(gameMode: GameMode, deckId: String): PromptDeck {
        val jsonString = Res.readBytes("files/decks/${gameMode.id}/$deckId.json").decodeToString()
        val deckJson = json.decodeFromString<DeckJson>(jsonString)
        return deckJson.toDomain()
    }

    @Deprecated("Use loadDeck(gameMode, deckId) instead")
    @OptIn(ExperimentalResourceApi::class)
    suspend fun loadDeck(deckId: String): PromptDeck {
        return loadDeck(GameMode.ToucanDrink, deckId)
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
