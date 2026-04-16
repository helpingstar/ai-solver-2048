package io.github.helpigstar.aisolver2048.ui.platform.components.util

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource

@Composable
fun rememberVectorPainter(
    @DrawableRes id: Int,
): VectorPainter {
    val image = ImageVector.vectorResource(id = id)
    return rememberVectorPainter(image)
}