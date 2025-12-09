package com.kelson.toucan.domain.models

sealed interface Prompt {
    val type: PromptType
    val numTargets: Int
    val text: String
}

data class NormalPrompt(
    override val text: String,
    override val numTargets: Int
) : Prompt {
    override val type: PromptType = PromptType.Normal
}

data class MinigamePrompt(
    override val text: String,
    override val numTargets: Int
) : Prompt {
    override val type: PromptType = PromptType.Minigame
}

data class VirusPrompt(
    override val text: String,
    val secondary: String
) : Prompt {
    override val type: PromptType = PromptType.Virus
    override val numTargets: Int = 0
}
