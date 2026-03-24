package io.github.helpigstar.aisolver2048.ui.platform.components.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
    Settings(
        label = "Settings",
        iconResId = AisolverDrawable.ic_settings,
    ),
}

object AisolverActionButtonDefaults {
    val Size = 48.dp
    val IconSize = 24.dp
    val Shape: Shape = RoundedCornerShape(10.dp)
    val ContentPadding = PaddingValues(12.dp)

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
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .size(AisolverActionButtonDefaults.Size)
            .semantics {
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
            },
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            disabledElevation = 0.dp,
        ),
        contentPadding = AisolverActionButtonDefaults.ContentPadding,
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
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
