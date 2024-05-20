package com.example.videosensorsample.presentation.listener

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class GyroscopeEventListener(
    private val onRotationX: (Float) -> Unit,
    private val onRotationZ: (Float) -> Unit
) : SensorEventListener {

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
                val rotationX = event.values[0]
                val rotationZ = event.values[2]
                onRotationX(rotationX)
                onRotationZ(rotationZ)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
