package com.kelson.toucan.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo

@Composable
fun isLandscape(): Boolean {
    val windowInfo = LocalWindowInfo.current
    val size = windowInfo.containerSize // IntSize
    return size.width > size.height
}
