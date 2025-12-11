package com.kelson.toucan.domain.haptics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType

class IOSHaptics : Haptics {

    private val impactGenerator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
    private val notificationGenerator = UINotificationFeedbackGenerator()

    override fun success() {
        notificationGenerator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)
    }

    override fun error() {
        notificationGenerator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeError)
    }

    override fun light() {
        impactGenerator.impactOccurred()
    }
}

@Composable
actual fun rememberHaptics(): Haptics {
    return remember { IOSHaptics() }
}
