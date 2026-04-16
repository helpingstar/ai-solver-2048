package io.github.helpigstar.aisolver2048.ui.platform.components.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.helpigstar.aisolver2048.ui.R as uiR
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme

@Composable
fun AisolverFilledButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFC55A3A),
            contentColor = Color.White,
            disabledContainerColor = defaultAisolverColorScheme.button.primaryBackgroundDisabled,
            disabledContentColor = defaultAisolverColorScheme.button.primaryForegroundDisabled,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 17.sp,
                lineHeight = 26.sp,
                fontFamily = FontFamily(Font(uiR.font.pretendard_semibold)),
            ),
        )
    }
}
