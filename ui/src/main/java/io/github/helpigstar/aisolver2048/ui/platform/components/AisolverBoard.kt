package io.github.helpigstar.aisolver2048.ui.platform.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.helpigstar.aisolver2048.core.model.MoveDirection
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

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
    animateTileChanges: Boolean = true,
    selectedPosition: AisolverBoardPosition? = null,
    onCellClick: ((AisolverBoardPosition) -> Unit)? = null,
    onSwipe: ((MoveDirection) -> Unit)? = null,
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
                    AnimatedBoardTile(
                        tile = tile,
                        animateTileChanges = animateTileChanges,
                    )
                }
            }
        }

        if (selectedPosition != null || onCellClick != null || onSwipe != null) {
            CellSelectionGrid(
                selectedPosition = selectedPosition,
                onCellClick = onCellClick,
                onSwipe = onSwipe,
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
    onSwipe: ((MoveDirection) -> Unit)?,
) {
    val interactionModifier = if (onCellClick == null && onSwipe == null) {
        Modifier
    } else {
        Modifier.pointerInput(onCellClick, onSwipe) {
            val cellSizePx = AisolverBoardDefaults.CellSize.toPx()
            val stepPx = (AisolverBoardDefaults.CellSize + AisolverBoardDefaults.CellGap).toPx()

            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val startPosition = down.position
                var totalHorizontalDrag = 0f
                var totalVerticalDrag = 0f
                var passedTouchSlop = false
                var pointerStillPressed = true

                while (pointerStillPressed) {
                    val event = awaitPointerEvent()
                    if (event.changes.count { pointerChange -> pointerChange.pressed } > 1) {
                        return@awaitEachGesture
                    }

                    val change = event.changes.firstOrNull { pointerChange ->
                        pointerChange.id == down.id
                    } ?: break

                    totalHorizontalDrag += change.position.x - change.previousPosition.x
                    totalVerticalDrag += change.position.y - change.previousPosition.y

                    if (
                        !passedTouchSlop &&
                        (
                                abs(totalHorizontalDrag) > viewConfiguration.touchSlop ||
                                        abs(totalVerticalDrag) > viewConfiguration.touchSlop
                                )
                    ) {
                        passedTouchSlop = true
                    }

                    if (passedTouchSlop) {
                        if (onSwipe != null) {
                            change.consume()
                            onSwipe(
                                resolveSwipeDirection(
                                    horizontalDistance = totalHorizontalDrag,
                                    verticalDistance = totalVerticalDrag,
                                ),
                            )
                            return@awaitEachGesture
                        }
                        change.consume()
                    }

                    pointerStillPressed = change.pressed
                }

                when {
                    !passedTouchSlop -> onCellClick?.let { handler ->
                        resolveTappedCell(
                            offset = startPosition,
                            cellSizePx = cellSizePx,
                            stepPx = stepPx,
                        )?.let(handler)
                    }
                }
            }
        }
    }

    Column(
        modifier = interactionModifier,
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
                        modifier = cellModifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedBoardTile(
    tile: AisolverBoardTile,
    animateTileChanges: Boolean,
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

        if (!animateTileChanges) {
            offsetX.snapTo(endX)
            offsetY.snapTo(endY)
            scale.snapTo(1f)
            return@LaunchedEffect
        }

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

private fun resolveSwipeDirection(
    horizontalDistance: Float,
    verticalDistance: Float,
): MoveDirection =
    if (abs(horizontalDistance) >= abs(verticalDistance)) {
        if (horizontalDistance >= 0f) {
            MoveDirection.Right
        } else {
            MoveDirection.Left
        }
    } else if (verticalDistance >= 0f) {
        MoveDirection.Down
    } else {
        MoveDirection.Up
    }

private fun resolveTappedCell(
    offset: Offset,
    cellSizePx: Float,
    stepPx: Float,
): AisolverBoardPosition? {
    if (offset.x < 0f || offset.y < 0f) return null

    val column = (offset.x / stepPx).toInt()
    val row = (offset.y / stepPx).toInt()
    if (row !in 0 until AisolverBoardDefaults.GridSize || column !in 0 until AisolverBoardDefaults.GridSize) {
        return null
    }

    val localX = offset.x - (column * stepPx)
    val localY = offset.y - (row * stepPx)
    if (localX >= cellSizePx || localY >= cellSizePx) {
        return null
    }

    return AisolverBoardPosition(
        row = row,
        column = column,
    )
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
