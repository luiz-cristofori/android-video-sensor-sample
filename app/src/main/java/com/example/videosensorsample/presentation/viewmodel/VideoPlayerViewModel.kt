package com.example.videosensorsample.presentation.viewmodel

import android.location.Location
import android.media.AudioManager
import android.media.AudioManager.STREAM_MUSIC
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.videosensorsample.domain.repository.VideoRepository
import com.example.videosensorsample.presentation.action.VideoPlayerAction
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.OnPermissionDenied
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.OnPermissionGranted
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.OnRequestSystemPermission
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.OnRotationX
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.OnRotationZ
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.OnShakeDetect
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.PausePlayer
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.RequestSystemPermission
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.ResetPlayer
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.ResumePlayer
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.UpdatePlaybackBy
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect.UpdateVolumeBy
import com.example.videosensorsample.presentation.state.VideoPlayerState
import com.example.videosensorsample.presentation.state.VideoPlayerState.Loaded
import com.example.videosensorsample.presentation.state.VideoPlayerState.Loading
import com.example.videosensorsample.presentation.state.VideoPlayerUiModel
import com.example.videosensorsample.presentation.viewmodel.helper.MutableSingleLiveEvent
import com.example.videosensorsample.presentation.viewmodel.helper.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class VideoPlayerViewModel(
    private val videoRepository: VideoRepository,
    private val audioManager: AudioManager
) : ViewModel() {
    companion object {
        private const val MAX_RANGE = 10
        private const val PLAYBACK_UPDATE_SENSIBILITY = 5000
    }

    private val _state = MutableLiveData<VideoPlayerState>(Loading(VideoPlayerUiModel()))
    val state = _state.asLiveData()

    private val _effect = MutableSingleLiveEvent<VideoPlayerUiEffect>()
    val effect = _effect.asSingleLiveEvent()

    private val _locationState = MutableStateFlow<Location?>(null)

    private var _videoPlayer: Player? = null

    fun sendAction(action: VideoPlayerAction.Action) {
        when (action) {
            is OnShakeDetect -> {
                _effect.value = if (_videoPlayer?.isPlaying == true) PausePlayer else ResumePlayer
            }

            is OnRotationX -> {
                _effect.value = UpdateVolumeBy(action.rotationX)
            }

            is OnRotationZ -> {
                _effect.value = UpdatePlaybackBy(action.rotationZ)
            }

            is OnPermissionDenied -> {
                _state.value = Loaded(
                    getCurrentStateModel().copy(
                        videoUrl = videoRepository.getVideoUrl(),
                        isEducationalDialogVisible = false,
                        isPermissionGranted = false
                    )
                )
            }

            is OnPermissionGranted -> {
                _state.value = Loaded(
                    getCurrentStateModel().copy(
                        videoUrl = videoRepository.getVideoUrl(),
                        isEducationalDialogVisible = false,
                        isPermissionGranted = true
                    )
                )
                getLocationUpdates()
            }

            is OnRequestSystemPermission -> {
                _effect.value = RequestSystemPermission
            }
        }
    }

    fun init(granted: Boolean, exoPlayer: ExoPlayer) {
        _videoPlayer = exoPlayer
        if (granted) {
            _state.value = Loaded(
                getCurrentStateModel().copy(
                    videoUrl = videoRepository.getVideoUrl(),
                    isPermissionGranted = true,
                    player = _videoPlayer
                )
            )
            getLocationUpdates()
            return
        }
        _state.value = Loading(
            getCurrentStateModel().copy(
                isEducationalDialogVisible = true,
                isPermissionGranted = false,
                player = _videoPlayer
            )
        )
    }

    fun pausePlayer() {
        _videoPlayer?.pause()
    }

    fun resumePlayer() {
        _videoPlayer?.play()
    }

    fun resetPlayer() {
        _videoPlayer?.run {
            stop()
            seekTo(0)
        }
    }

    fun updateVolumeBy(volume: Float) {
        val currentVolume = audioManager.getStreamVolume(STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(STREAM_MUSIC)
        val newVolume = currentVolume + (volume * 2).toInt()

        audioManager.setStreamVolume(STREAM_MUSIC, newVolume.coerceIn(0, maxVolume), 0)
    }

    fun updatePlaybackBy(rotationZ: Float) {
        _videoPlayer?.run {
            if (isLoading || isPlaying) return
            val newPosition =
                currentPosition + (rotationZ * PLAYBACK_UPDATE_SENSIBILITY).toLong()
            if (newPosition < 0) seekTo(0)
            if (newPosition > duration) seekTo(duration)
            seekTo(newPosition)
        }
    }

    override fun onCleared() {
        super.onCleared()
        _videoPlayer?.release()
    }

    private fun getLocationUpdates() {
        viewModelScope.launch {
            videoRepository.getLocationUpdates().distinctUntilChanged()
                .collect { currentLocation ->
                    val previousLocation = _locationState.value
                    if (previousLocation != null) {
                        val distance = currentLocation.distanceTo(previousLocation)
                        if (distance > MAX_RANGE) {
                            _effect.value = ResetPlayer
                        }
                    }
                    _locationState.value = currentLocation
                }
        }
    }

    private fun getCurrentStateModel() = checkNotNull(state.value).uiModel
}
