package io.github.helpigstar.aisolver2048.ui.platform.base.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.github.helpigstar.aisolver2048.ui.platform.base.BaseViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun <E> EventsEffect(
    viewModel: BaseViewModel<*, E, *>,
    lifecycleOwner: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    handler: (E) -> Unit,
) {
    LaunchedEffect(key1 = Unit) {
        viewModel
            .eventFlow
            .filter { event ->
                event is BackgroundEvent ||
                        lifecycleOwner.currentState.isAtLeast(Lifecycle.State.RESUMED)
            }
            .onEach { event ->
                if (event is DeferredBackgroundEvent) {
                    launch {
                        lifecycleOwner
                            .currentStateFlow
                            .first { it.isAtLeast(Lifecycle.State.RESUMED) }
                        handler(event)
                    }
                } else {
                    handler(event)
                }
            }
            .launchIn(this)
    }
}
