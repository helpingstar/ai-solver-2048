package io.github.helpigstar.aisolver2048.ui.platform.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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

object AisolverAnalyzeButtonDefaults {
    val MinWidth = 161.dp
    val MinHeight = 44.dp
    val IconSize = 24.dp
    val Shape: Shape = RoundedCornerShape(14.dp)

    val ContainerColor: Color = defaultAisolverColorScheme.button.primaryBackground
    val DisabledContainerColor: Color = defaultAisolverColorScheme.button.primaryBackgroundDisabled
    val ContentColor: Color = defaultAisolverColorScheme.button.primaryForeground
    val DisabledContentColor: Color = defaultAisolverColorScheme.button.primaryForegroundDisabled
}

@Composable
fun AisolverAnalyzeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String = "Analyze",
    contentDescription: String? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = AisolverAnalyzeButtonDefaults.Shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = AisolverAnalyzeButtonDefaults.ContainerColor,
            contentColor = AisolverAnalyzeButtonDefaults.ContentColor,
            disabledContainerColor = AisolverAnalyzeButtonDefaults.DisabledContainerColor,
            disabledContentColor = AisolverAnalyzeButtonDefaults.DisabledContentColor,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            disabledElevation = 0.dp,
        ),
        contentPadding = PaddingValues(0.dp),
    ) {
        Box(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(vertical = 10.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = AisolverDrawable.ic_analyze),
                    contentDescription = null,
                    modifier = Modifier.size(AisolverAnalyzeButtonDefaults.IconSize),
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverAnalyzeButtonPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AisolverAnalyzeButton(onClick = {})
            AisolverAnalyzeButton(
                onClick = {},
                enabled = false,
            )
        }
    }
}
