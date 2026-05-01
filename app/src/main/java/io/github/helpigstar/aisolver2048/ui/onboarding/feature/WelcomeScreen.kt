package io.github.helpigstar.aisolver2048.ui.onboarding.feature

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.helpigstar.aisolver2048.ui.R as uiR
import io.github.helpigstar.aisolver2048.ui.platform.base.util.EventsEffect
import io.github.helpigstar.aisolver2048.ui.platform.components.scaffold.AisolverScaffold
import io.github.helpigstar.aisolver2048.ui.platform.components.util.rememberVectorPainter
import io.github.helpigstar.aisolver2048.ui.platform.resource.AisolverString
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { state.pages.size })
    val scope = rememberCoroutineScope()

    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is WelcomeEvent.UpdatePager -> {
                scope.launch { pagerState.animateScrollToPage(event.index) }
            }
        }
    }

    AisolverScaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = defaultAisolverColorScheme.background.primary
    ) {
        WelcomeScreenContent(
            state = state,
            pagerState = pagerState,
            onPagerSwipe = { viewModel.trySendAction(WelcomeAction.PagerSwipe(it)) },
            onDotClick = { viewModel.trySendAction(WelcomeAction.DotClick(it)) },
            onGetStartedClick = { viewModel.trySendAction(WelcomeAction.StartClick) },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun WelcomeScreenContent(
    state: WelcomeState,
    pagerState: PagerState,
    onPagerSwipe: (Int) -> Unit,
    onDotClick: (Int) -> Unit,
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(pagerState.currentPage) {
        onPagerSwipe(pagerState.currentPage)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.weight(1f))

        HorizontalPager(state = pagerState) { index ->
            WelcomeCardCompact(
                state = state.pages[index],
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IndicatorDots(
            selectedIndexProvider = { state.index },
            totalCount = state.pages.size,
            onDotClick = onDotClick,
            modifier = Modifier
                .padding(bottom = 32.dp)
                .height(44.dp)
        )

        val startButtonLabel = stringResource(id = AisolverString.welcome_start_button)
        Button(
            onClick = onGetStartedClick,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .semantics(mergeDescendants = true) {
                    contentDescription = startButtonLabel
                },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC55A3A),
                contentColor = Color.White,
            ),
            contentPadding = PaddingValues(16.dp),
        ) {
            Text(
                text = startButtonLabel,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 17.sp,
                    lineHeight = 26.sp,
                    fontFamily = FontFamily(Font(uiR.font.pretendard_semibold)),
                ),
                color = Color.White,
                modifier = Modifier.semantics { hideFromAccessibility() },
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun WelcomeCardCompact(
    state: WelcomeState.WelcomeCard,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .semantics(mergeDescendants = true) {},
    ) {
        Image(
            painter = rememberVectorPainter(id = state.imageRes),
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit,
            contentDescription = null,
            modifier = Modifier.size(240.dp),
        )

        Text(
            text = stringResource(id = state.titleRes),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 42.sp,
                lineHeight = 46.sp,
                fontFamily = FontFamily(Font(uiR.font.pretendard_semibold)),
            ),
            color = Color(0xFF1F1A17),
            modifier = Modifier
                .padding(
                    top = 48.dp,
                    bottom = 16.dp,
                ),
        )

        Text(
            text = stringResource(id = state.messageRes),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 17.sp,
                lineHeight = 26.sp,
                fontFamily = FontFamily(Font(uiR.font.pretendard_regular)),
            ),
            color = Color(0xFF6B635E),
        )
    }
}

@Composable
private fun IndicatorDots(
    selectedIndexProvider: () -> Int,
    totalCount: Int,
    onDotClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        items(totalCount) { index ->
            val isSelected = index == selectedIndexProvider()
            val color by animateColorAsState(
                targetValue = if (isSelected) Color(0xFFC55A3A) else Color(0xFFD1D5DB),
                label = "dotColor"
            )
            val width by animateDpAsState(
                targetValue = if (isSelected) 32.dp else 8.dp,
                animationSpec = tween(durationMillis = 200),
                label = "dotWidth"
            )

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onDotClick(index) }
            )
        }
    }
}
