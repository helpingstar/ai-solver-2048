package io.github.helpigstar.aisolver2048.ui.platform.components.valuepickerbottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme
import io.github.helpigstar.aisolver2048.ui.platform.components.actionbutton.AisolverActionButton
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverUiTokens

@Composable
fun AisolverValuePickerBottomSheet(
    modifier: Modifier = Modifier,
    showExtendedValues: Boolean = true,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = AisolverUiTokens.BoardBackground,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .background(
                        color = AisolverUiTokens.BaseTile.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(99.dp)
                    )
                    .padding(horizontal = 22.dp, vertical = 3.dp)
            )
            Text(
                text = "Select value",
                style = MaterialTheme.typography.titleMedium,
                color = AisolverUiTokens.BrightText,
                fontWeight = FontWeight.Bold
            )
            ValueRows(values = listOf(null, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024))
            if (showExtendedValues) {
                Text(
                    text = "More values",
                    style = MaterialTheme.typography.labelLarge,
                    color = AisolverUiTokens.BaseTile
                )
                ValueRows(values = listOf(2048, 4096, 8192))
            } else {
                AisolverActionButton(
                    text = "더 보기",
                    onClick = {},
                    compact = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 760)
@Composable
private fun AisolverValuePickerBottomSheet_preview() {
    AisolverPreviewTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AisolverValuePickerBottomSheet(showExtendedValues = false)
            AisolverValuePickerBottomSheet(showExtendedValues = true)
        }
    }
}

@Composable
private fun ValueRows(values: List<Int?>) {
    values.chunked(5).forEach { rowValues ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rowValues.forEach { value ->
                ValueChip(
                    value = value,
                    modifier = Modifier.weight(1f)
                )
            }
            repeat(5 - rowValues.size) {
                Box(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ValueChip(
    value: Int?,
    modifier: Modifier = Modifier,
) {
    val appearance = value?.let {
        AisolverUiTokens.tileAppearance(it, isCompact = true)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AisolverUiTokens.TileRadius),
        color = appearance?.background ?: AisolverUiTokens.BaseTile
    ) {
        Box(modifier = Modifier.padding(vertical = 14.dp)) {
            Text(
                text = value?.toString() ?: "Empty",
                modifier = Modifier.align(Alignment.Center),
                color = appearance?.contentColor ?: AisolverUiTokens.PrimaryText,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
