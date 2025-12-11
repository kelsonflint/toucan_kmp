package com.kelson.toucan.domain.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class AndroidTiltSensor(context: Context) : TiltSensor, SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _tiltDirection = MutableStateFlow(TiltDirection.NEUTRAL)
    override val tiltDirection: Flow<TiltDirection> = _tiltDirection

    private var lastDirection = TiltDirection.NEUTRAL

    override fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun stop() {
        sensorManager.unregisterListener(this)
        _tiltDirection.value = TiltDirection.NEUTRAL
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            // In landscape mode with phone held like a forehead sign:
            // Z-axis: negative when face down, positive when face up
            val z = it.values[2]

            // Use a threshold to avoid jitter
            val tiltThreshold = 6.0f

            val newDirection = when {
                // Phone tilted forward/down - correct answer
                z < -tiltThreshold -> TiltDirection.TILTED_DOWN
                // Phone tilted backward/up - skip
                z > tiltThreshold -> TiltDirection.TILTED_UP
                else -> TiltDirection.NEUTRAL
            }

            // Only emit when direction changes to avoid spam
            if (newDirection != lastDirection) {
                lastDirection = newDirection
                _tiltDirection.value = newDirection
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }
}

@Composable
actual fun rememberTiltSensor(): TiltSensor {
    val context = LocalContext.current
    return remember { AndroidTiltSensor(context) }
}
