package io.github.helpigstar.aisolver2048.ui.platform.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme

object AisolverTileDefaults {
    val Size = 74.dp
    val Shape = RoundedCornerShape(14.dp)
}

@Composable
fun AisolverTile(
    value: Int,
    modifier: Modifier = Modifier,
    size: Dp = AisolverTileDefaults.Size,
) {
    val tileColors = tileColorsFor(value = value)
    val fontSize = tileFontSize(value = value)

    Box(
        modifier = modifier
            .size(size)
            .background(
                color = tileColors.containerColor,
                shape = AisolverTileDefaults.Shape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value.toString(),
            color = tileColors.contentColor,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = fontSize,
                lineHeight = fontSize,
            ),
        )
    }
}

private data class TileColors(
    val containerColor: Color,
    val contentColor: Color,
)

private fun tileColorsFor(value: Int): TileColors {
    val tileColors = defaultAisolverColorScheme.tile

    val containerColor = when (value) {
        2 -> tileColors.value2
        4 -> tileColors.value4
        8 -> tileColors.value8
        16 -> tileColors.value16
        32 -> tileColors.value32
        64 -> tileColors.value64
        128 -> tileColors.value128
        256 -> tileColors.value256
        512 -> tileColors.value512
        1024 -> tileColors.value1024
        2048 -> tileColors.value2048
        else -> tileColors.valueSuper
    }

    val contentColor = when (value) {
        2, 4 -> tileColors.textDark
        else -> tileColors.textLight
    }

    return TileColors(
        containerColor = containerColor,
        contentColor = contentColor,
    )
}

private fun tileFontSize(value: Int): TextUnit {
    val digitCount = value.toString().length
    return (36 - (digitCount * 4)).sp
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverTilePreview() {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AisolverTile(value = 2)
        AisolverTile(value = 16)
        AisolverTile(value = 128)
        AisolverTile(value = 2048)
    }
}
