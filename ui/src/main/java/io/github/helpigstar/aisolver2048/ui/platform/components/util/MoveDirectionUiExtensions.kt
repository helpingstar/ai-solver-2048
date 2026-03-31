package io.github.helpigstar.aisolver2048.ui.platform.components.util

import io.github.helpigstar.aisolver2048.core.model.MoveDirection
import io.github.helpigstar.aisolver2048.ui.platform.resource.AisolverDrawable

val MoveDirection.label: String
    get() = when (this) {
        MoveDirection.Up -> "Up"
        MoveDirection.Right -> "Right"
        MoveDirection.Left -> "Left"
        MoveDirection.Down -> "Down"
    }

val MoveDirection.iconResId: Int
    get() = when (this) {
        MoveDirection.Up -> AisolverDrawable.ic_upward
        MoveDirection.Right -> AisolverDrawable.ic_forward
        MoveDirection.Left -> AisolverDrawable.ic_backward
        MoveDirection.Down -> AisolverDrawable.ic_downward
    }