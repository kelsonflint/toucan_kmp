package com.kelson.toucan.domain.sensor

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow

enum class TiltDirection {
    NEUTRAL,
    TILTED_DOWN,   // Phone tilted forward/down - correct answer
    TILTED_UP      // Phone tilted back/up - skip/pass
}

interface TiltSensor {
    fun start()
    fun stop()
    val tiltDirection: Flow<TiltDirection>
}

@Composable
expect fun rememberTiltSensor(): TiltSensor
