package com.kelson.toucan.domain.sensor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue
import kotlin.math.PI

class IOSTiltSensor : TiltSensor {

    private val motionManager = CMMotionManager()
    private val _tiltDirection = MutableStateFlow(TiltDirection.NEUTRAL)
    override val tiltDirection: Flow<TiltDirection> = _tiltDirection

    private var lastDirection = TiltDirection.NEUTRAL

    @OptIn(ExperimentalForeignApi::class)
    override fun start() {
        lastDirection = TiltDirection.NEUTRAL
        _tiltDirection.value = TiltDirection.NEUTRAL

        motionManager.deviceMotionUpdateInterval = 1.0 / 30.0

        motionManager.startDeviceMotionUpdatesToQueue(
            NSOperationQueue.mainQueue()
        ) { motion, error ->

            if (error != null || motion == null) return@startDeviceMotionUpdatesToQueue

            val roll = motion.attitude.roll // radians, rotation along long edge
            val threshold = 0.35  // ~20 degrees

            val newDirection = when {
                roll > PI - threshold -> TiltDirection.TILTED_DOWN  // top tilts toward ground
                roll < threshold -> TiltDirection.TILTED_UP   // top tilts up
                else -> TiltDirection.NEUTRAL
            }

            if (newDirection != lastDirection) {
                if (lastDirection == TiltDirection.NEUTRAL || newDirection == TiltDirection.NEUTRAL) {
                    lastDirection = newDirection
                    _tiltDirection.value = newDirection
                }
            }


        }
    }

    override fun stop() {
        motionManager.stopDeviceMotionUpdates()
    }

}

@Composable
actual fun rememberTiltSensor(): TiltSensor {
    return remember { IOSTiltSensor() }
}

