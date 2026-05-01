package io.github.helpigstar.aisolver2048.ui.platform.components.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.button.color.aisolverTonalIconButtonColors
import io.github.helpigstar.aisolver2048.ui.platform.components.util.rememberVectorPainter
import io.github.helpigstar.aisolver2048.ui.platform.resource.AisolverDrawable

private val AisolverTonalIconButtonSize = 48.dp
private val AisolverTonalIconButtonIconSize = 24.dp

/**
 * A tonal icon button that displays an icon.
 *
 * @param vectorIconRes Icon to display on the button.
 * @param contentDescription The content description for this icon button.
 * @param onClick Callback for when the icon button is clicked.
 * @param modifier A [Modifier] for the composable.
 * @param isEnabled Whether the button should be enabled.
 */
@Composable
fun AisolverTonalIconButton(
    @DrawableRes vectorIconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) {
    FilledIconButton(
        modifier = modifier
            .size(AisolverTonalIconButtonSize)
            .semantics(mergeDescendants = true) {
                this.contentDescription = contentDescription
            },
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = aisolverTonalIconButtonColors(),
        enabled = isEnabled,
    ) {
        Icon(
            painter = rememberVectorPainter(id = vectorIconRes),
            contentDescription = null,
            modifier = Modifier.size(AisolverTonalIconButtonIconSize),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverTonalIconButtonPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AisolverTonalIconButton(
                vectorIconRes = AisolverDrawable.ic_undo,
                contentDescription = "Undo",
                onClick = {},
            )
            AisolverTonalIconButton(
                vectorIconRes = AisolverDrawable.ic_autorenew,
                contentDescription = "Reset",
                onClick = {},
            )
            AisolverTonalIconButton(
                vectorIconRes = AisolverDrawable.ic_undo,
                contentDescription = "Undo",
                onClick = {},
                isEnabled = false,
            )
        }
    }
}
