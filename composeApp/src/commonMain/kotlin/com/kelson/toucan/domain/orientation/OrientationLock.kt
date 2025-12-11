package com.kelson.toucan.domain.orientation

import androidx.compose.runtime.Composable

interface OrientationLock {
    fun lockLandscape()
    fun unlock()
}

@Composable
expect fun rememberOrientationLock(): OrientationLock
