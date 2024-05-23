package com.example.videosensorsample.presentation.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_GYROSCOPE
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_UI
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.example.videosensorsample.R
import com.example.videosensorsample.domain.CacheUtil
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.OnPermissionDenied
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.OnPermissionGranted
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.OnRotationX
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.OnRotationZ
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.OnShakeDetect
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.PausePlayer
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.RequestSystemPermission
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.ResetPlayer
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.ResumePlayer
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.UpdatePlaybackBy
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.UpdateVolumeBy
import com.example.videosensorsample.presentation.listener.AccelerometerEventListener
import com.example.videosensorsample.presentation.listener.GyroscopeEventListener
import com.example.videosensorsample.presentation.ui.theme.VideoSensorSampleTheme
import com.example.videosensorsample.presentation.viewmodel.VideoPlayerViewModel
import com.example.videosensorsample.presentation.viewmodel.helper.collectEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

@UnstableApi
class VideoPlayerActivity : ComponentActivity() {

    private val viewModel: VideoPlayerViewModel by viewModel()
    private val sensorManager: SensorManager by inject()

    private var accelerometerSensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null

    private lateinit var gyroscopeEventListener: GyroscopeEventListener
    private lateinit var accelerometerEventListener: AccelerometerEventListener

    @androidx.annotation.OptIn(UnstableApi::class)
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cacheDataSourceFactory = CacheUtil.getCacheDataSourceFactory(this)

        initSensors()

        setContent {
            VideoSensorSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val gpsPermissionState = rememberPermissionState(ACCESS_FINE_LOCATION)
                    val requestPermissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted ->
                            viewModel.sendAction(if (isGranted) OnPermissionGranted else OnPermissionDenied)
                        }
                    )

                    val exoPlayer =
                        ExoPlayer.Builder(this)
                            .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
                            .build().apply {
                                playWhenReady = true
                                prepare()
                            }

                    LaunchedEffect(Unit) {
                        viewModel.init(gpsPermissionState.status.isGranted, exoPlayer)
                    }

                    viewModel.effect.collectEffect { currentEffect ->
                        currentEffect?.let { effect ->
                            when (effect) {
                                is PausePlayer -> viewModel.pausePlayer()

                                is ResumePlayer -> viewModel.resumePlayer()

                                is UpdateVolumeBy -> viewModel.updateVolumeBy(effect.volume)

                                is RequestSystemPermission -> requestPermissionLauncher.launch(
                                    ACCESS_FINE_LOCATION
                                )

                                is ResetPlayer -> {
                                    Toast.makeText(
                                        this@VideoPlayerActivity,
                                        getString(R.string.too_far_away_toast_text), LENGTH_SHORT
                                    ).show()
                                    viewModel.resetPlayer()
                                }

                                is UpdatePlaybackBy -> viewModel.updatePlaybackBy(effect.seekBy)

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

    private fun initSensors() {
        accelerometerSensor = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
        gyroscopeSensor = sensorManager.getDefaultSensor(TYPE_GYROSCOPE)
        initSensorsListeners()
    }

    private fun initSensorsListeners() {
        accelerometerEventListener =
            AccelerometerEventListener { viewModel.sendAction(OnShakeDetect) }
        gyroscopeEventListener = GyroscopeEventListener(
            onRotationX = {
                viewModel.sendAction(OnRotationX(it))
            },
            onRotationZ = {
                viewModel.sendAction(OnRotationZ(it))
            })
    }

    override fun onResume() {
        super.onResume()
        accelerometerSensor?.let {
            sensorManager.registerListener(accelerometerEventListener, it, SENSOR_DELAY_UI)
        }
        gyroscopeSensor?.let {
            sensorManager.registerListener(
                gyroscopeEventListener,
                it,
                SENSOR_DELAY_UI
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.let {
            it.unregisterListener(accelerometerEventListener)
            it.unregisterListener(gyroscopeEventListener)
        }
    }
}
