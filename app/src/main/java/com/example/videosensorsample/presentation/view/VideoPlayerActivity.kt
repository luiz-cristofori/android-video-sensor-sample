package com.example.videosensorsample.presentation.view

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
import com.example.videosensorsample.presentation.effect.collectEffect
import com.example.videosensorsample.presentation.ui.theme.VideoSensorSampleTheme
import com.example.videosensorsample.presentation.viewmodel.VideoPlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoPlayerActivity : ComponentActivity() {

    private val viewModel: VideoPlayerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoSensorSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    LaunchedEffect(Unit) {
                        viewModel.loadVideoUrl()
                    }

                    viewModel.effect.collectEffect { currentEffect ->
                        currentEffect?.let { effect ->
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
}