package com.kelson.toucan.domain.orientation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSNumber
import platform.Foundation.setValue
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation
import platform.UIKit.UIInterfaceOrientationMaskLandscapeLeft
import platform.UIKit.UIInterfaceOrientationMaskAll

class IOSOrientationLock : OrientationLock {

    override fun lockLandscape() {
        // Force rotation to landscape right (clockwise from portrait)
        OrientationHelper.lockToLandscapeRight()
    }

    override fun unlock() {
        OrientationHelper.unlockOrientation()
    }
}

// Singleton to manage orientation state that can be accessed from AppDelegate
object OrientationHelper {
    var lockedOrientation: Int? = null
        private set

    fun lockToLandscapeRight() {
        lockedOrientation = UIInterfaceOrientationMaskLandscapeLeft.toInt()
        // Trigger orientation update using NSKeyValueCoding
        // Note: UIDeviceOrientationLandscapeLeft results in UIInterfaceOrientationLandscapeRight (they're inverted)
        val orientationValue = NSNumber(long = UIDeviceOrientation.UIDeviceOrientationLandscapeLeft.value.toLong())
        UIDevice.currentDevice.setValue(orientationValue, forKey = "orientation")
    }

    fun unlockOrientation() {
        lockedOrientation = null
        // Trigger orientation update to allow rotation back to portrait
        val orientationValue = NSNumber(long = UIDeviceOrientation.UIDeviceOrientationPortrait.value.toLong())
        UIDevice.currentDevice.setValue(orientationValue, forKey = "orientation")
    }

    fun getSupportedOrientations(): Int {
        return lockedOrientation ?: UIInterfaceOrientationMaskAll.toInt()
    }
}

@Composable
actual fun rememberOrientationLock(): OrientationLock {
    return remember { IOSOrientationLock() }
}
