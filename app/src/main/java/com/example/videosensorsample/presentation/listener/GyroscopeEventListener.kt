package com.example.videosensorsample.presentation.listener

import android.hardware.Sensor
import android.hardware.Sensor.TYPE_GYROSCOPE
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import kotlin.math.abs

class GyroscopeEventListener(
    private val onRotationX: (Float) -> Unit,
    private val onRotationZ: (Float) -> Unit
) : SensorEventListener {
    companion object {
        const val THRESHOLD = 0.7f
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (event.sensor.type == TYPE_GYROSCOPE) {
                val rotationX = event.values[0]
                val rotationZ = event.values[2]
                if (abs(rotationX) > THRESHOLD) {
                    onRotationX(rotationX)
                }
                if (abs(rotationZ) > THRESHOLD) {
                    onRotationZ(rotationZ)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
