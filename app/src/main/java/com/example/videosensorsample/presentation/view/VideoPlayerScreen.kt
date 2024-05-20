package com.example.videosensorsample.presentation.view

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.SHOW_BUFFERING_ALWAYS
import com.example.videosensorsample.R
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.OnPermissionDenied
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.OnRequestSystemPermission
import com.example.videosensorsample.presentation.state.VideoPlayerState
import com.example.videosensorsample.presentation.state.VideoPlayerState.Loaded
import com.example.videosensorsample.presentation.state.VideoPlayerState.Loading
import com.example.videosensorsample.presentation.state.VideoPlayerUiModel
import com.example.videosensorsample.presentation.ui.theme.VideoSensorSampleTheme

private const val ASPECT_RATIO = 16 / 9f

@Composable
fun VideoPlayerScreen(state: VideoPlayerState, sendAction: (Action) -> Unit) {
    when (state) {
        is Loading -> LoadingContent(state.uiModel, sendAction)

        is Loaded -> VideoPlayerContent(state.uiModel, sendAction)
    }
}

@Composable
fun LoadingContent(uiModel: VideoPlayerUiModel, sendAction: (Action) -> Unit) {
    AnimatedVisibility(uiModel.isEducationalDialogVisible) {
        EducationalPermissionDialog(sendAction)
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun EducationalPermissionDialog(sendAction: (Action) -> Unit) {
    AlertDialog(
        onDismissRequest = { sendAction(OnPermissionDenied) },
        title = { Text(text = stringResource(R.string.permission_alert_dialog_title)) },
        text = { Text(text = stringResource(R.string.permission_alert_dialog_description)) },
        confirmButton = {
            Button(
                onClick = { sendAction(OnRequestSystemPermission) }
            ) {
                Text(text = stringResource(R.string.permission_alert_dialog_confirm_button_text))
            }
        },
        dismissButton = {
            Button(onClick = { sendAction(OnPermissionDenied) }) {
                Text(text = stringResource(R.string.permission_alert_dialog_cancel_button_text))
            }
        }
    )
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerContent(uiModel: VideoPlayerUiModel, sendAction: (Action) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var lifecycle by remember { mutableStateOf(ON_CREATE) }

    val mediaSource = remember(uiModel.videoUrl) {
        MediaItem.fromUri(uiModel.videoUrl.orEmpty())
    }

    LaunchedEffect(uiModel.player) {
        uiModel.player?.setMediaItem(mediaSource)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            uiModel.player?.release()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = CenterHorizontally
    ) {
        AnimatedVisibility(uiModel.isPermissionGranted.not()) {
            Button(onClick = { sendAction(OnRequestSystemPermission) }) {
                Text(text = stringResource(R.string.enable_permission_button_text))
            }
        }
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .aspectRatio(ASPECT_RATIO),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    setShowBuffering(SHOW_BUFFERING_ALWAYS)
                    player = uiModel.player
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
