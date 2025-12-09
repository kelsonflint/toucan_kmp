package com.kelson.toucan.domain.models

data class PromptResult(
    val text: String,
    val type: PromptType,
    val isVirusCure: Boolean = false,
    val hasActiveVirus: Boolean = false
)
