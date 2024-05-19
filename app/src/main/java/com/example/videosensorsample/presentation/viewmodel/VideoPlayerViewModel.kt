package com.example.videosensorsample.presentation.viewmodel

import android.media.AudioManager
import android.media.AudioManager.STREAM_MUSIC
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


class VideoPlayerViewModel(
    private val videoRepository: VideoRepository,
    private val audioManager: AudioManager
) : ViewModel() {

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
                _effect.value = UpdateVolumeBy(action.rotationX)
            }
        }
    }

    fun initPlayer(player: ExoPlayer) {
        _videoPlayer = player
        _state.value = Loading(getCurrentStateModel().copy(player = _videoPlayer))
    }

    fun pausePlayer() {
        _videoPlayer?.pause()
    }

    fun resumePlayer() {
        _videoPlayer?.play()
    }

    fun updateVolumeBy(volume: Float) {
        val currentVolume = audioManager.getStreamVolume(STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(STREAM_MUSIC)

        var newVolume = currentVolume + (volume * 2).toInt()
        newVolume = newVolume.coerceIn(0, maxVolume)

        audioManager.setStreamVolume(STREAM_MUSIC, newVolume, 0)
    }

    fun loadVideoUrl() {
        _state.value = Loaded(
            getCurrentStateModel().copy(
                videoUrl = videoRepository.getVideoUrl()
            )
        )
    }


    override fun onCleared() {
        super.onCleared()
        _videoPlayer?.release()
    }

    private fun getCurrentStateModel() = checkNotNull(state.value).uiModel
}

fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> = this
