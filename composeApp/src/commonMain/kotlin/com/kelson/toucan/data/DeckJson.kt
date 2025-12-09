package com.kelson.toucan.data

import kotlinx.serialization.Serializable

@Serializable
data class DeckJson(
    val id: String,
    val name: String,
    val description: String,
    val prompts: List<PromptJson>
)

@Serializable
data class PromptJson(
    val type: String,
    val text: String,
    val numTargets: Int = 0,
    val secondary: String? = null
)
