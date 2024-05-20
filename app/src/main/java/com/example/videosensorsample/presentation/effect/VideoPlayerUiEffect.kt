package com.example.videosensorsample.presentation.effect

sealed class VideoPlayerUiEffect {
    data object PausePlayer : VideoPlayerUiEffect()
    data object ResumePlayer : VideoPlayerUiEffect()
    data object ResetPlayer : VideoPlayerUiEffect()
    data class UpdateVolumeBy(val volume: Float) : VideoPlayerUiEffect()
    data class UpdatePlaybackBy(val seekBy: Float) : VideoPlayerUiEffect()
    data object RequestSystemPermission : VideoPlayerUiEffect()
}
