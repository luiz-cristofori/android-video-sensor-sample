package com.example.videosensorsample.presentation.effect

sealed class VideoPlayerUiEffect {
    data object PausePlayer : VideoPlayerUiEffect()
    data object ResumePlayer : VideoPlayerUiEffect()
    data class UpdateVolumeBy(val volume: Float) : VideoPlayerUiEffect()
}
