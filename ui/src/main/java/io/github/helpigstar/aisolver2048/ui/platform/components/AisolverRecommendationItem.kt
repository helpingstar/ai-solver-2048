package io.github.helpigstar.aisolver2048.ui.platform.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.helpigstar.aisolver2048.core.model.MoveDirection
import io.github.helpigstar.aisolver2048.ui.platform.components.util.iconResId
import io.github.helpigstar.aisolver2048.ui.platform.components.util.label
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme
import java.util.Locale
import kotlin.math.floor

object AisolverRecommendationItemDefaults {
    val IconChipSize = 40.dp
    val IconSize = 24.dp
    val HorizontalPadding = 16.dp
    val VerticalPadding = 8.dp
    val IconSpacing = 16.dp
    val ItemHeight = IconChipSize + (VerticalPadding * 2)
    val DividerThickness = 1.dp
    val IconChipShape = RoundedCornerShape(14.dp)
    const val ValueAnimationDurationMillis = 300

    val IconChipColor = defaultAisolverColorScheme.background.utility
    val LabelColor = defaultAisolverColorScheme.text.secondary
    val ConfidenceColor = defaultAisolverColorScheme.text.primary
    val DividerColor = defaultAisolverColorScheme.stroke.divider
}

@Composable
fun AisolverRecommendationItem(
    direction: MoveDirection,
    confidencePercent: Float,
    modifier: Modifier = Modifier,
    animateValueChanges: Boolean = true,
    onClick: (() -> Unit)? = null,
    showDivider: Boolean = true,
) {
    val resolvedConfidencePercent =
        confidencePercent.coerceIn(minimumValue = 0f, maximumValue = 100f)
    val animatedConfidencePercent = animateFloatAsState(
        targetValue = resolvedConfidencePercent,
        animationSpec = if (animateValueChanges) {
            tween(durationMillis = AisolverRecommendationItemDefaults.ValueAnimationDurationMillis)
        } else {
            snap()
        },
        label = "recommendationConfidencePercent",
    )

    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AisolverRecommendationItemDefaults.ItemHeight)
                .then(
                    if (onClick == null) {
                        Modifier
                    } else {
                        Modifier.clickable(onClick = onClick)
                    }
                )
                .padding(
                    horizontal = AisolverRecommendationItemDefaults.HorizontalPadding,
                    vertical = AisolverRecommendationItemDefaults.VerticalPadding,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(AisolverRecommendationItemDefaults.IconChipSize)
                    .clip(AisolverRecommendationItemDefaults.IconChipShape)
                    .background(AisolverRecommendationItemDefaults.IconChipColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = direction.iconResId),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(AisolverRecommendationItemDefaults.IconSize),
                )
            }
            Spacer(modifier = Modifier.width(AisolverRecommendationItemDefaults.IconSpacing))
            Text(
                text = direction.label,
                color = AisolverRecommendationItemDefaults.LabelColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = animatedConfidencePercent.value.toDisplayPercent(),
                color = AisolverRecommendationItemDefaults.ConfidenceColor,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                ),
            )
        }

        if (showDivider) {
            HorizontalDivider(
                thickness = AisolverRecommendationItemDefaults.DividerThickness,
                color = AisolverRecommendationItemDefaults.DividerColor,
            )
        }
    }
}

private fun Float.toDisplayPercent(): String {
    val flooredPercent = floor(this * 10f) / 10f
    return String.format(Locale.US, "%.1f%%", flooredPercent)
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverRecommendationItemPreview() {
    MaterialTheme {
        AisolverRecommendationItem(
            direction = MoveDirection.Up,
            confidencePercent = 78f,
            modifier = Modifier
                .padding(16.dp)
                .width(353.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverRecommendationItemListPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .width(353.dp)
                .background(defaultAisolverColorScheme.background.primary),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            AisolverRecommendationItem(
                direction = MoveDirection.Left,
                confidencePercent = 78f,
            )
            AisolverRecommendationItem(
                direction = MoveDirection.Right,
                confidencePercent = 78f,
            )
            AisolverRecommendationItem(
                direction = MoveDirection.Up,
                confidencePercent = 78f,
            )
            AisolverRecommendationItem(
                direction = MoveDirection.Down,
                confidencePercent = 78f,
            )
        }
    }
}
