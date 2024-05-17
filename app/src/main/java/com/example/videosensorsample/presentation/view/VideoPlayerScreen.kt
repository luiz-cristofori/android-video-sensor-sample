package com.example.videosensorsample.presentation.view

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.SHOW_BUFFERING_ALWAYS
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action
import com.example.videosensorsample.presentation.state.VideoPlayerState
import com.example.videosensorsample.presentation.state.VideoPlayerState.Loaded
import com.example.videosensorsample.presentation.state.VideoPlayerState.Loading
import com.example.videosensorsample.presentation.state.VideoPlayerUiModel
import com.example.videosensorsample.presentation.ui.theme.VideoSensorSampleTheme

@Composable
fun VideoPlayerScreen(state: VideoPlayerState, sendAction: (Action) -> Unit) {
    when (state) {
        is Loading -> {
            LoadingContent()
        }

        is Loaded -> {
            VideoPlayerContent(state.uiModel)
        }
    }
}

@Composable
fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerContent(uiModel: VideoPlayerUiModel) {
    var lifecycle by remember { mutableStateOf(ON_CREATE) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val context = LocalContext.current

    val mediaSource = remember(uiModel.videoUrl) {
        MediaItem.fromUri(uiModel.videoUrl.orEmpty())
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            this.setMediaItem(mediaSource)
            this.playWhenReady = true
            this.prepare()
            this.addListener(object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == ExoPlayer.STATE_READY) {

                    }
                }
            })
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            exoPlayer.release()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f),
        factory = { ctx ->
            PlayerView(ctx).apply {
                setShowBuffering(SHOW_BUFFERING_ALWAYS)
                player = exoPlayer
            }
        },
        update = {
            when (lifecycle) {
                ON_PAUSE -> {
                    it.onPause()
                    it.player?.pause()
                }

                ON_RESUME -> {
                    it.onResume()
                }

                else -> Unit
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VideoPlayerScreenPreview() {
    VideoSensorSampleTheme {
        VideoPlayerScreen(
            state = Loaded(VideoPlayerUiModel(videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4")),
            sendAction = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VideoPlayerLoadingScreenPreview() {
    VideoSensorSampleTheme {
        VideoPlayerScreen(
            state = Loading(VideoPlayerUiModel()),
            sendAction = {}
        )
    }
}

