package com.example.videosensorsample.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Observer
import com.example.videosensorsample.presentation.viewmodel.helper.SingleLiveEvent

/**
 * The function collectEffect will be used to get effect
 * while [SingleLiveEvent] is used to represent one shot events in our architecture.
 *
 * Implementation example:
 * ```
 * // UiEffect
 * sealed class MyUiEffect {
 *   object OpenScreenBAfterAsyncRequest : MyUiEffect()
 * }
 *
 * // ViewModel
 * private val _uiEffect = MutableSingleLiveEvent<MyUiEffect>()
 * val uiEffect = _uiEffect.asSingleLiveEvent()
 *
 * // Compose
 * viewModel.uiEffect.collectEffect {
 *   when (it) {
 *     MyUiEffect.OpenScreenBAfterAsyncRequest -> openScreen()
 *   }
 * }
 * ```
 *
 */
@Composable
fun <T> SingleLiveEvent<T>.collectEffect(block: (T?) -> Unit) {
    val lifecycle = LocalLifecycleOwner.current
    DisposableEffect(key1 = this) {
        val observer = Observer<T> { block(it) }
        this@collectEffect.observe(lifecycle, observer)

        onDispose {
            this@collectEffect.removeObserver(observer)
        }
    }
}
