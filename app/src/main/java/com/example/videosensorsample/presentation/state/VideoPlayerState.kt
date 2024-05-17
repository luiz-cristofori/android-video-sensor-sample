package com.example.videosensorsample.presentation.state

sealed class VideoPlayerState(open val uiModel: VideoPlayerUiModel = VideoPlayerUiModel()) {
    data class Loading(override val uiModel: VideoPlayerUiModel) : VideoPlayerState(uiModel)
    data class Loaded(override val uiModel: VideoPlayerUiModel) : VideoPlayerState(uiModel)
}

data class VideoPlayerUiModel(
    val videoUrl: String? = null
)