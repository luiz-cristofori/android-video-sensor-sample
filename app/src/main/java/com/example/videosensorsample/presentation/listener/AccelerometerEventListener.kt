package com.example.videosensorsample.presentation.listener

import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager.GRAVITY_EARTH
import kotlin.math.sqrt

class AccelerometerEventListener(private val onShake: () -> Unit) : SensorEventListener {
    companion object {
        const val SHAKE_THRESHOLD_GRAVITY = 2.7f
        const val SHAKE_TIME_LAPSE = 500
    }

    private var lastShakeTime: Long = 0

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (event.sensor.type == TYPE_ACCELEROMETER) {
                val gX = event.values[0] / GRAVITY_EARTH
                val gY = event.values[1] / GRAVITY_EARTH
                val gZ = event.values[2] / GRAVITY_EARTH

                val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

                if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                    val now = System.currentTimeMillis()
                    if (lastShakeTime + SHAKE_TIME_LAPSE > now) {
                        return
                    }
                    lastShakeTime = now
                    onShake()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
