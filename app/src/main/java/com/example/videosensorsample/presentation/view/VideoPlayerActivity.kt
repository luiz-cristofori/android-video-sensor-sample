package com.example.videosensorsample.presentation.view

import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_GYROSCOPE
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_NORMAL
import android.hardware.SensorManager.SENSOR_DELAY_UI
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.media3.exoplayer.ExoPlayer
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.PausePlayer
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.ResumePlayer
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.UpdateVolumeBy
import com.example.videosensorsample.presentation.effect.collectEffect
import com.example.videosensorsample.presentation.ui.theme.VideoSensorSampleTheme
import com.example.videosensorsample.presentation.viewmodel.VideoPlayerViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoPlayerActivity : ComponentActivity() {

    private val viewModel: VideoPlayerViewModel by viewModel()
    private val sensorManager: SensorManager by inject()

    private var accelerometerSensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null

    private lateinit var gyroscopeEventListener: GyroscopeEventListener
    private lateinit var shakeDetector: ShakeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accelerometerSensor = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
        gyroscopeSensor = sensorManager.getDefaultSensor(TYPE_GYROSCOPE)

        shakeDetector = ShakeDetector { viewModel.sendAction(Action.OnShakeDetect) }
        gyroscopeEventListener =
            GyroscopeEventListener { viewModel.sendAction(Action.OnRotationX(it)) }

        setContent {
            VideoSensorSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val exoPlayer = ExoPlayer.Builder(this).build().apply {
                        this.playWhenReady = true
                        this.prepare()
                    }

                    LaunchedEffect(Unit) {
                        viewModel.initPlayer(exoPlayer)
                        viewModel.loadVideoUrl()
                    }

                    viewModel.effect.collectEffect { currentEffect ->
                        currentEffect?.let { effect ->
                            when (effect) {
                                is PausePlayer -> viewModel.pausePlayer()

                                is ResumePlayer -> viewModel.resumePlayer()

                                is UpdateVolumeBy -> viewModel.updateVolumeBy(effect.volume)
                            }
                        }
                    }

                    val state by viewModel.state.observeAsState()

                    VideoPlayerScreen(
                        state = checkNotNull(state),
                        sendAction = viewModel::sendAction
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometerSensor?.let {
            sensorManager.registerListener(shakeDetector, it, SENSOR_DELAY_UI)
        }
        gyroscopeSensor?.let {
            sensorManager.registerListener(
                gyroscopeEventListener,
                it,
                SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.let {
            it.unregisterListener(shakeDetector)
            it.unregisterListener(gyroscopeEventListener)
        }
    }
}
