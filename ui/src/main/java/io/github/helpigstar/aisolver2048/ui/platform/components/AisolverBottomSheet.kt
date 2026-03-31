package io.github.helpigstar.aisolver2048.ui.platform.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme

sealed interface AisolverBottomSheetItem {
    data object Clear : AisolverBottomSheetItem

    data class Value(val value: Int) : AisolverBottomSheetItem
}

object AisolverBottomSheetDefaults {
    val SheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    val SheetBackgroundColor: Color = defaultAisolverColorScheme.background.primary
    val GridWidth = 332.dp
    val GridSpacing = 12.dp
    val ContentTopPadding = 8.dp
    val ContentBottomPadding = 34.dp
    val TileShadowShape = RoundedCornerShape(14.dp)
    val TileShadowElevation = 4.dp
    val ClearTileColor: Color = defaultAisolverColorScheme.board.cell
    val ClearTileTextColor: Color = defaultAisolverColorScheme.text.boardPrimary

    val DefaultItems: List<AisolverBottomSheetItem> = buildList {
        add(AisolverBottomSheetItem.Clear)
        addAll(
            listOf(
                2,
                4,
                8,
                16,
                32,
                64,
                128,
                256,
                512,
                1024,
                2048,
                4096,
                8192,
                16384,
                32768,
            ).map(AisolverBottomSheetItem::Value),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisolverBottomSheet(
    onDismissRequest: () -> Unit,
    onItemClick: (AisolverBottomSheetItem) -> Unit,
    modifier: Modifier = Modifier,
    items: List<AisolverBottomSheetItem> = AisolverBottomSheetDefaults.DefaultItems,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = AisolverBottomSheetDefaults.SheetShape,
        containerColor = AisolverBottomSheetDefaults.SheetBackgroundColor,
    ) {
        AisolverBottomSheetContent(
            items = items,
            onItemClick = onItemClick,
        )
    }
}

@Composable
private fun AisolverBottomSheetContent(
    items: List<AisolverBottomSheetItem>,
    onItemClick: (AisolverBottomSheetItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AisolverBottomSheetDefaults.SheetBackgroundColor)
            .padding(
                top = AisolverBottomSheetDefaults.ContentTopPadding,
                bottom = AisolverBottomSheetDefaults.ContentBottomPadding,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AisolverBottomSheetDefaults.GridSpacing),
        ) {
            items.chunked(size = 4).forEach { rowItems ->
                Row(
                    modifier = Modifier.width(AisolverBottomSheetDefaults.GridWidth),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = AisolverBottomSheetDefaults.GridSpacing,
                        alignment = Alignment.CenterHorizontally,
                    ),
                ) {
                    rowItems.forEach { item ->
                        when (item) {
                            AisolverBottomSheetItem.Clear -> {
                                ClearTileButton(
                                    onClick = { onItemClick(item) },
                                )
                            }

                            is AisolverBottomSheetItem.Value -> {
                                TileButton(
                                    value = item.value,
                                    onClick = { onItemClick(item) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TileButton(
    value: Int,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
    ) {
        AisolverTile(value = value)
    }
}

@Composable
private fun ClearTileButton(
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .background(
                color = AisolverBottomSheetDefaults.ClearTileColor,
                shape = AisolverBottomSheetDefaults.TileShadowShape,
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(AisolverTileDefaults.Size)
                .background(
                    color = AisolverBottomSheetDefaults.ClearTileColor,
                    shape = AisolverTileDefaults.Shape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "×",
                color = AisolverBottomSheetDefaults.ClearTileTextColor,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 40.sp,
                    lineHeight = 40.sp,
                ),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverBottomSheetContentPreview() {
    MaterialTheme {
        AisolverBottomSheetContent(
            items = AisolverBottomSheetDefaults.DefaultItems,
            onItemClick = {},
        )
    }
}
