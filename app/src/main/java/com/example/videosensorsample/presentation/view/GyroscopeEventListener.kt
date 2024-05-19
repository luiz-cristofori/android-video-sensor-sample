package com.example.videosensorsample.presentation.view

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class GyroscopeEventListener(private val onRotationX: (Float) -> Unit) : SensorEventListener {

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
                val rotationX = event.values[0]
                onRotationX(rotationX)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
