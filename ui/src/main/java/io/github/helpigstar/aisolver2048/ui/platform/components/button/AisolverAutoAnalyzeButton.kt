package io.github.helpigstar.aisolver2048.ui.platform.components.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.helpigstar.aisolver2048.ui.platform.resource.AisolverDrawable
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme

enum class AisolverAutoAnalyzeButtonVariant(
    val label: String,
    val iconResId: Int,
    val containerColor: Color,
    val contentColor: Color,
) {
    Auto(
        label = "Auto",
        iconResId = AisolverDrawable.ic_play_circle,
        containerColor = defaultAisolverColorScheme.button.autoAnalyzeBackground,
        contentColor = defaultAisolverColorScheme.button.autoAnalyzeForeground,
    ),
    Stop(
        label = "Stop",
        iconResId = AisolverDrawable.ic_stop_circle,
        containerColor = defaultAisolverColorScheme.button.stopBackground,
        contentColor = defaultAisolverColorScheme.button.stopForeground,
    ),
}

object AisolverAutoAnalyzeButtonDefaults {
    val Shape: Shape = RoundedCornerShape(14.dp)
    val ContentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    val IconSize = 24.dp
    val ContentSpacing = 4.dp
}

@Composable
fun AisolverAutoAnalyzeButton(
    variant: AisolverAutoAnalyzeButtonVariant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = AisolverAutoAnalyzeButtonDefaults.Shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = variant.containerColor,
            contentColor = variant.contentColor,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            disabledElevation = 0.dp,
        ),
        contentPadding = AisolverAutoAnalyzeButtonDefaults.ContentPadding,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AisolverAutoAnalyzeButtonDefaults.ContentSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = variant.iconResId),
                contentDescription = null,
                modifier = Modifier.size(AisolverAutoAnalyzeButtonDefaults.IconSize),
            )
            Text(
                text = variant.label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                ),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverAutoAnalyzeButtonPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            AisolverAutoAnalyzeButton(
                variant = AisolverAutoAnalyzeButtonVariant.Auto,
                onClick = {},
            )
            AisolverAutoAnalyzeButton(
                variant = AisolverAutoAnalyzeButtonVariant.Stop,
                onClick = {},
            )
        }
    }
}
