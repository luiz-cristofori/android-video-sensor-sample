package com.example.videosensorsample.presentation.state

import androidx.media3.common.Player

sealed class VideoPlayerState(open val uiModel: VideoPlayerUiModel = VideoPlayerUiModel()) {
    data class Loading(override val uiModel: VideoPlayerUiModel) : VideoPlayerState(uiModel)
    data class Loaded(override val uiModel: VideoPlayerUiModel) : VideoPlayerState(uiModel)
}

data class VideoPlayerUiModel(
    val videoUrl: String? = null,
    val player: Player? = null
)