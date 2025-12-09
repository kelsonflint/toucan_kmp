package com.kelson.toucan.domain.models

data class PromptDeck(
    val id: String,
    val name: String,
    val description: String,
    val prompts: List<Prompt>
)

data class DeckInfo(
    val id: String,
    val name: String,
    val description: String,
    val promptCount: Int
)
