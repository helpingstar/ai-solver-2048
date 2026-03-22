package io.github.helpigstar.aisolver2048.ui.platform.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.resource.AisolverDrawable
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme

enum class AisolverActionButtonVariant(
    val label: String,
    val iconResId: Int,
) {
    Undo(
        label = "Undo",
        iconResId = AisolverDrawable.ic_undo,
    ),
    Reset(
        label = "Reset",
        iconResId = AisolverDrawable.ic_autorenew,
    ),
}

object AisolverActionButtonDefaults {
    val Size = 48.dp
    val IconSize = 24.dp
    val Shape: Shape = RoundedCornerShape(10.dp)

    val ContainerColor: Color = defaultAisolverColorScheme.button.utilityBackground
    val DisabledContainerColor: Color = defaultAisolverColorScheme.button.utilityBackgroundDisabled
    val ContentColor: Color = Color(0xFF1F1F1F)
    val DisabledContentColor: Color = defaultAisolverColorScheme.button.utilityForegroundDisabled
}

@Composable
fun AisolverActionButton(
    variant: AisolverActionButtonVariant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String = variant.label,
) {
    AisolverActionButton(
        iconResId = variant.iconResId,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentDescription = contentDescription,
    )
}

@Composable
fun AisolverActionButton(
    iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
    containerColor: Color = AisolverActionButtonDefaults.ContainerColor,
    contentColor: Color = AisolverActionButtonDefaults.ContentColor,
    disabledContainerColor: Color = AisolverActionButtonDefaults.DisabledContainerColor,
    disabledContentColor: Color = AisolverActionButtonDefaults.DisabledContentColor,
    shape: Shape = AisolverActionButtonDefaults.Shape,
) {
    val resolvedContainerColor = if (enabled) {
        containerColor
    } else {
        disabledContainerColor
    }
    val resolvedContentColor = if (enabled) {
        contentColor
    } else {
        disabledContentColor
    }

    Box(
        modifier = modifier
            .size(AisolverActionButtonDefaults.Size)
            .clip(shape)
            .background(resolvedContainerColor)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics {
                role = Role.Button
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
                if (!enabled) {
                    disabled()
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = resolvedContentColor,
            modifier = Modifier.size(AisolverActionButtonDefaults.IconSize),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverActionButtonPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AisolverActionButton(
                variant = AisolverActionButtonVariant.Undo,
                onClick = {},
            )
            AisolverActionButton(
                variant = AisolverActionButtonVariant.Reset,
                onClick = {},
            )
            AisolverActionButton(
                variant = AisolverActionButtonVariant.Undo,
                onClick = {},
                enabled = false,
            )
        }
    }
}
