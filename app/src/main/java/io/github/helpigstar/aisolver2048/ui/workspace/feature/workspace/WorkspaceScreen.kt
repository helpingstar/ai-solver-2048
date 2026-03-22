package io.github.helpigstar.aisolver2048.ui.workspace.feature.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoard
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardDefaults
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardPosition
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardTile
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverGameActions
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverHeader
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendation
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendationCard
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendationDirection
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverScoreCard
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverScoreCardDefaults
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme
import io.github.helpigstar.aisolver2048.ui.theme.AiSolver2048Theme

private val sampleBoardValues = listOf(
    2, 4, 8, 16,
    32, 64, 128, 256,
    512, 1024, 2048, 4096,
    8192, 16384, 32768, 65536,
)

private val sampleBoardTiles = sampleBoardValues.mapIndexed { index, value ->
    AisolverBoardTile(
        id = "tile-$index",
        value = value,
        position = AisolverBoardPosition(
            row = index / 4,
            column = index % 4,
        ),
    )
}

private val sampleRecommendations = listOf(
    AisolverRecommendation(
        direction = AisolverRecommendationDirection.Up,
        confidencePercent = 78,
    ),
    AisolverRecommendation(
        direction = AisolverRecommendationDirection.Right,
        confidencePercent = 15,
    ),
    AisolverRecommendation(
        direction = AisolverRecommendationDirection.Left,
        confidencePercent = 15,
    ),
    AisolverRecommendation(
        direction = AisolverRecommendationDirection.Down,
        confidencePercent = 15,
    ),
)

@Composable
fun WorkspaceScreen(
    viewModel: WorkspaceViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    WorkspaceScreen(state = state)
}

@Composable
private fun WorkspaceScreen(
    state: WorkspaceState,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = defaultAisolverColorScheme.background.primary,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 12.dp,
            ),
        ) {
            WorkspaceStatusSection(score = 1024)
            AisolverBoard(tiles = sampleBoardTiles)
            AisolverRecommendationCard(
                recommendations = sampleRecommendations,
                onAnalyzeClick = {},
                modifier = Modifier.width(AisolverBoardDefaults.BoardSize),
            )
        }
    }
}

@Composable
private fun WorkspaceStatusSection(
    score: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(defaultAisolverColorScheme.background.primary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AisolverScoreCard(
                score = score,
                modifier = Modifier.width(AisolverScoreCardDefaults.MinWidth),
            )
            AisolverGameActions(
                onUndoClick = {},
                onResetClick = {},
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun WorkspaceScreenPreview() {
    AiSolver2048Theme {
        WorkspaceScreen(
            state = WorkspaceState(
                title = "AI Solver 2048",
                description = "온보딩이 완료된 후 진입하는 메인 화면입니다.",
            ),
        )
    }
}
