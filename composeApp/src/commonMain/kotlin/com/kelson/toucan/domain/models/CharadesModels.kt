package com.kelson.toucan.domain.models

data class CharadesPrompt(
    val text: String,
    val wasCorrect: Boolean? = null // null = not answered yet, true = correct, false = skipped
)

data class CharadesRoundResult(
    val prompts: List<CharadesPrompt>,
    val correctCount: Int,
    val skippedCount: Int,
    val totalAttempted: Int
) {
    val score: Int get() = correctCount
}
