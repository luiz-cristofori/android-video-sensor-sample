package com.example.videosensorsample.presentation.action

interface VideoPlayerAction {
    fun sendAction(action: Action)

    sealed class Action {
        object ClickTest : Action()
        object OnShakeDetect : Action()
        data class OnRotationX(val rotationX: Float) : Action()
    }
}
