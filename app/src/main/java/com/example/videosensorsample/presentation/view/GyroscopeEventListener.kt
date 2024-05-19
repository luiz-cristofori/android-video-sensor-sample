package com.example.videosensorsample.presentation.view

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log

class GyroscopeEventListener(private val onRotationX: (Float) -> Unit) : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                val rotationX = event.values[0]
                onRotationX(rotationX)
                Log.d("Sensor", "rotate X by : $rotationX")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }
}