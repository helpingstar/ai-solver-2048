package io.github.helpigstar.aisolver2048.ui.platform.components.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.helpigstar.aisolver2048.ui.platform.components.button.color.aisolverFilledButtonColors

private val AisolverFilledButtonShape = RoundedCornerShape(14.dp)
private val AisolverFilledButtonIconSize = 24.dp
private val AisolverFilledButtonIconSpacing = 4.dp
private val AisolverFilledButtonContentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)

@Composable
fun AisolverFilledButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    isEnabled: Boolean = true,
    colors: ButtonColors = aisolverFilledButtonColors(),
    contentDescription: String = label,
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier.semantics(mergeDescendants = true) {
            this.contentDescription = contentDescription
        },
        shape = AisolverFilledButtonShape,
        colors = colors,
        contentPadding = AisolverFilledButtonContentPadding,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AisolverFilledButtonIconSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(AisolverFilledButtonIconSize),
                )
            }
            Text(
                text = label,
                modifier = Modifier.semantics { hideFromAccessibility() },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                ),
            )
        }
    }
}
