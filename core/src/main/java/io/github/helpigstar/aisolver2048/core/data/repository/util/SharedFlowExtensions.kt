package io.github.helpigstar.aisolver2048.core.data.repository.util

import kotlinx.coroutines.flow.MutableSharedFlow

fun <T> bufferedMutableSharedFlow(
    replay: Int = 0,
): MutableSharedFlow<T> =
    MutableSharedFlow(
        replay = replay,
        extraBufferCapacity = Int.MAX_VALUE,
    )