package com.example.videosensorsample.presentation.action

interface VideoPlayerAction {
    fun sendAction(action: Action)

    sealed class Action {
        data object OnShakeDetect : Action()
        data class OnRotationX(val rotationX: Float) : Action()
        data class OnRotationZ(val rotationZ: Float) : Action()
        data object OnPermissionDenied : Action()
        data object OnPermissionGranted : Action()
        data object OnRequestSystemPermission : Action()
    }
}
