package com.example.videosensorsample.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.videosensorsample.domain.repository.VideoRepository
import com.example.videosensorsample.presentation.action.VideoPlayerAction
import com.example.videosensorsample.presentation.action.VideoPlayerAction.Action.ClickTest
import com.example.videosensorsample.presentation.effect.VideoPlayerUiEffect
import com.example.videosensorsample.presentation.state.VideoPlayerState
import com.example.videosensorsample.presentation.state.VideoPlayerState.Loaded
import com.example.videosensorsample.presentation.state.VideoPlayerState.Loading
import com.example.videosensorsample.presentation.state.VideoPlayerUiModel

class VideoPlayerViewModel(private val videoRepository: VideoRepository) : ViewModel() {

    private val _state = MutableLiveData<VideoPlayerState>(Loading(VideoPlayerUiModel()))
    val state = _state.asLiveData()

    private val _effect = MutableSingleLiveEvent<VideoPlayerUiEffect>()
    val effect = _effect.asSingleLiveEvent()

    fun sendAction(action: VideoPlayerAction.Action) {
        when (action) {
            is ClickTest -> {

            }
        }
    }

    fun loadVideoUrl() {
        _state.value = Loaded(getCurrentStateModel().copy(videoUrl = videoRepository.getVideoUrl()))
    }

    private fun getCurrentStateModel() = checkNotNull(state.value).uiModel
}

fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> = this
