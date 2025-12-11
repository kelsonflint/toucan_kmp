package com.kelson.toucan.domain.haptics

import androidx.compose.runtime.Composable

interface Haptics {
    fun success()
    fun error()
    fun light()
}

@Composable
expect fun rememberHaptics(): Haptics
