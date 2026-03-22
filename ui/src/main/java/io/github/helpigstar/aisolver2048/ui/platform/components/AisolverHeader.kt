package io.github.helpigstar.aisolver2048.ui.platform.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme

object AisolverHeaderDefaults {
    val Height = 36.dp

    val ContainerColor = defaultAisolverColorScheme.background.primary
    val TitleColor = defaultAisolverColorScheme.text.primary
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AisolverHeader(
    modifier: Modifier = Modifier,
    title: String = "AI Solver 2048",
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = AisolverHeaderDefaults.TitleColor,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                ),
                maxLines = 1,
            )
        },
        modifier = modifier,
        expandedHeight = AisolverHeaderDefaults.Height,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AisolverHeaderDefaults.ContainerColor,
            scrolledContainerColor = AisolverHeaderDefaults.ContainerColor,
            titleContentColor = AisolverHeaderDefaults.TitleColor,
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverHeaderPreview() {
    MaterialTheme {
        AisolverHeader(
            modifier = Modifier.width(355.dp),
        )
    }
}
