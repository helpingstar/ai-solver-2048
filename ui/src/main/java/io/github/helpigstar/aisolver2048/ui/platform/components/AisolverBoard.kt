package io.github.helpigstar.aisolver2048.ui.platform.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme
import kotlin.math.roundToInt
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object AisolverBoardDefaults {
    const val GridSize = 4

    val BoardSize = 356.dp
    val BoardPadding = 12.dp
    val CellSize = 74.dp
    val CellGap = 12.dp

    val BoardShape = RoundedCornerShape(16.dp)
    val CellShape = RoundedCornerShape(14.dp)

    const val MoveDurationMillis = 180
    const val SpawnDurationMillis = 140
    const val MergePopDurationMillis = 120
}

data class AisolverBoardPosition(
    val row: Int,
    val column: Int,
)

enum class AisolverBoardTileMotionState {
    Static,
    Spawned,
    Merged,
}

data class AisolverBoardTile(
    val id: String,
    val value: Int,
    val position: AisolverBoardPosition,
    val previousPosition: AisolverBoardPosition? = null,
    val motionState: AisolverBoardTileMotionState = AisolverBoardTileMotionState.Static,
    val zIndex: Float = defaultZIndexFor(motionState = motionState),
) {
    companion object {
        private fun defaultZIndexFor(
            motionState: AisolverBoardTileMotionState,
        ): Float = when (motionState) {
            AisolverBoardTileMotionState.Static -> 1f
            AisolverBoardTileMotionState.Spawned -> 2f
            AisolverBoardTileMotionState.Merged -> 3f
        }
    }
}

@Composable
fun AisolverBoard(
    tiles: List<AisolverBoardTile>,
    modifier: Modifier = Modifier,
    boardColor: Color = defaultAisolverColorScheme.board.background,
    emptyCellColor: Color = defaultAisolverColorScheme.board.cell,
    selectedPosition: AisolverBoardPosition? = null,
    onCellClick: ((AisolverBoardPosition) -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .requiredSize(AisolverBoardDefaults.BoardSize)
            .clip(AisolverBoardDefaults.BoardShape)
            .background(boardColor)
            .padding(AisolverBoardDefaults.BoardPadding),
    ) {
        EmptyBoardGrid(emptyCellColor = emptyCellColor)

        Box {
            tiles.forEach { tile ->
                key(tile.id) {
                    AnimatedBoardTile(tile = tile)
                }
            }
        }

        if (selectedPosition != null || onCellClick != null) {
            CellSelectionGrid(
                selectedPosition = selectedPosition,
                onCellClick = onCellClick,
            )
        }
    }
}

@Composable
private fun EmptyBoardGrid(
    emptyCellColor: Color,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AisolverBoardDefaults.CellGap),
    ) {
        repeat(AisolverBoardDefaults.GridSize) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AisolverBoardDefaults.CellGap),
            ) {
                repeat(AisolverBoardDefaults.GridSize) {
                    Box(
                        modifier = Modifier
                            .size(AisolverBoardDefaults.CellSize)
                            .background(
                                color = emptyCellColor,
                                shape = AisolverBoardDefaults.CellShape,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun CellSelectionGrid(
    selectedPosition: AisolverBoardPosition?,
    onCellClick: ((AisolverBoardPosition) -> Unit)?,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AisolverBoardDefaults.CellGap),
    ) {
        repeat(AisolverBoardDefaults.GridSize) { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(AisolverBoardDefaults.CellGap),
            ) {
                repeat(AisolverBoardDefaults.GridSize) { column ->
                    val position = AisolverBoardPosition(row = row, column = column)
                    val isSelected = position == selectedPosition
                    val cellModifier = Modifier
                        .size(AisolverBoardDefaults.CellSize)
                        .background(
                            color = if (isSelected) {
                                defaultAisolverColorScheme.board.cellSelected
                            } else {
                                Color.Transparent
                            },
                            shape = AisolverBoardDefaults.CellShape,
                        )

                    Box(
                        modifier = if (onCellClick == null) {
                            cellModifier
                        } else {
                            cellModifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onCellClick(position) },
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedBoardTile(
    tile: AisolverBoardTile,
) {
    val density = LocalDensity.current
    val targetOffset = boardOffsetFor(position = tile.position)
    val initialOffset = boardOffsetFor(position = tile.previousPosition ?: tile.position)

    val offsetX = remember(tile.id) {
        Animatable(with(density) { initialOffset.first.toPx() })
    }
    val offsetY = remember(tile.id) {
        Animatable(with(density) { initialOffset.second.toPx() })
    }
    val scale = remember(tile.id) {
        Animatable(initialScaleFor(motionState = tile.motionState))
    }

    LaunchedEffect(
        tile.id,
        tile.position,
        tile.previousPosition,
        tile.motionState,
        density,
    ) {
        val startX = with(density) { initialOffset.first.toPx() }
        val startY = with(density) { initialOffset.second.toPx() }
        val endX = with(density) { targetOffset.first.toPx() }
        val endY = with(density) { targetOffset.second.toPx() }

        offsetX.snapTo(startX)
        offsetY.snapTo(startY)

        if (startX != endX || startY != endY) {
            coroutineScope {
                launch {
                    offsetX.animateTo(
                        targetValue = endX,
                        animationSpec = tween(
                            durationMillis = AisolverBoardDefaults.MoveDurationMillis,
                            easing = FastOutSlowInEasing,
                        ),
                    )
                }
                launch {
                    offsetY.animateTo(
                        targetValue = endY,
                        animationSpec = tween(
                            durationMillis = AisolverBoardDefaults.MoveDurationMillis,
                            easing = FastOutSlowInEasing,
                        ),
                    )
                }
            }
        }

        when (tile.motionState) {
            AisolverBoardTileMotionState.Static -> {
                scale.snapTo(1f)
            }

            AisolverBoardTileMotionState.Spawned -> {
                scale.snapTo(0f)
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = AisolverBoardDefaults.SpawnDurationMillis,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }

            AisolverBoardTileMotionState.Merged -> {
                scale.snapTo(0.92f)
                scale.animateTo(
                    targetValue = 1.08f,
                    animationSpec = tween(
                        durationMillis = AisolverBoardDefaults.MergePopDurationMillis,
                        easing = FastOutSlowInEasing,
                    ),
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = AisolverBoardDefaults.MergePopDurationMillis,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = offsetX.value.roundToInt(),
                    y = offsetY.value.roundToInt(),
                )
            }
            .zIndex(tile.zIndex),
    ) {
        ScaledTile(
            value = tile.value,
            scale = scale.value,
        )
    }
}

@Composable
private fun ScaledTile(
    value: Int,
    scale: Float,
) {
    Box(
        modifier = Modifier
            .size(AisolverBoardDefaults.CellSize)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
    ) {
        AisolverTile(value = value)
    }
}

private fun boardOffsetFor(
    position: AisolverBoardPosition,
): Pair<Dp, Dp> {
    val step = AisolverBoardDefaults.CellSize + AisolverBoardDefaults.CellGap
    return Pair(
        first = step * position.column,
        second = step * position.row,
    )
}

private fun initialScaleFor(
    motionState: AisolverBoardTileMotionState,
): Float = when (motionState) {
    AisolverBoardTileMotionState.Static -> 1f
    AisolverBoardTileMotionState.Spawned -> 0f
    AisolverBoardTileMotionState.Merged -> 0.92f
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverBoardPreview() {
    var showMoved by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showMoved = true
    }

    val tiles = if (showMoved) {
        listOf(
            AisolverBoardTile(
                id = "move-2",
                value = 2,
                position = AisolverBoardPosition(row = 0, column = 3),
                previousPosition = AisolverBoardPosition(row = 0, column = 0),
            ),
            AisolverBoardTile(
                id = "merge-128",
                value = 128,
                position = AisolverBoardPosition(row = 1, column = 2),
                previousPosition = AisolverBoardPosition(row = 1, column = 1),
                motionState = AisolverBoardTileMotionState.Merged,
            ),
            AisolverBoardTile(
                id = "spawn-4",
                value = 4,
                position = AisolverBoardPosition(row = 3, column = 3),
                motionState = AisolverBoardTileMotionState.Spawned,
            ),
        )
    } else {
        emptyList()
    }

    AisolverBoard(
        tiles = tiles,
        modifier = Modifier.padding(16.dp),
    )
}
