package com.kelson.toucan.domain.orientation

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class AndroidOrientationLock(private val activity: Activity) : OrientationLock {

    private var previousOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    override fun lockLandscape() {
        previousOrientation = activity.requestedOrientation
        // LANDSCAPE_REVERSE = clockwise rotation from portrait (home button on left)
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
    }

    override fun unlock() {
        activity.requestedOrientation = previousOrientation
    }
}

@Composable
actual fun rememberOrientationLock(): OrientationLock {
    val context = LocalContext.current
    return remember { AndroidOrientationLock(context as Activity) }
}
