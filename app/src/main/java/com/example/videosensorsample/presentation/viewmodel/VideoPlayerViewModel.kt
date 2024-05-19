package com.example.videosensorsample.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.videosensorsample.domain.repository.VideoRepository
import com.example.videosensorsample.presentation.action.VideoPlayerAction
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.ClickTest
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.OnRotationX
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.OnShakeDetect
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.PausePlayer
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.ResumePlayer
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.UpdateVolumeBy
import com.example.videosensorsample.presentation.state.VideoPlayerState
import com.example.videosensorsample.presentation.state.VideoPlayerState.Loaded
import com.example.videosensorsample.presentation.state.VideoPlayerState.Loading
import com.example.videosensorsample.presentation.state.VideoPlayerUiModel
import com.example.videosensorsample.presentation.viewmodel.helper.MutableSingleLiveEvent


class VideoPlayerViewModel(private val videoRepository: VideoRepository) : ViewModel() {

    private val _state = MutableLiveData<VideoPlayerState>(Loading(VideoPlayerUiModel()))
    val state = _state.asLiveData()

    private val _effect = MutableSingleLiveEvent<VideoPlayerUiEffect>()
    val effect = _effect.asSingleLiveEvent()

    private var _videoPlayer: Player? = null

    fun sendAction(action: VideoPlayerAction.Action) {
        when (action) {
            is ClickTest -> {

            }

            is OnShakeDetect -> {
                _effect.value = if (_videoPlayer?.isPlaying == true) PausePlayer else ResumePlayer
            }

            is OnRotationX -> {
                _effect.value = UpdateVolumeBy(calculateVolume(action.rotationX))
            }
        }
    }

    fun initPlayer(player: ExoPlayer) {
        _videoPlayer = player
    }

    fun pausePlayer() {
        _videoPlayer?.pause()
    }

    fun resumePlayer() {
        _videoPlayer?.play()
    }

    fun updateVolumeBy(volume: Float) {
        _videoPlayer?.volume = volume
    }

    fun loadVideoUrl() {
        _state.value = Loaded(
            getCurrentStateModel().copy(
                videoUrl = videoRepository.getVideoUrl(),
                player = _videoPlayer
            )
        )
    }

    private fun calculateVolume(rotationX: Float): Float {
        return ((rotationX + 1.0f) / 2.0f).coerceIn(0.0f, 1.0f)
    }


    override fun onCleared() {
        super.onCleared()
        _videoPlayer?.release()
    }

    private fun getCurrentStateModel() = checkNotNull(state.value).uiModel
}

fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> = this
