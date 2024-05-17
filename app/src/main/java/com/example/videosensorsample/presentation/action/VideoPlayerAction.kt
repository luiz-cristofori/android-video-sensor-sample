package com.example.videosensorsample.presentation.action

interface VideoPlayerAction {
    fun sendAction(action: Action)

    sealed class Action {
        object ClickTest : Action()
        // data class Click(val videoUrl: String?) : Action()
    }
}
