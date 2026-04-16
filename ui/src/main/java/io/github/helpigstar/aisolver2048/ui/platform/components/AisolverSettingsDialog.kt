package io.github.helpigstar.aisolver2048.ui.platform.components

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.helpigstar.aisolver2048.ui.platform.components.button.AisolverTextButton
import io.github.helpigstar.aisolver2048.ui.platform.resource.AisolverString
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme

private val MaterialSwitchTrackColor = Color(0xFF6750A4)

object AisolverSettingsDialogDefaults {
    val DialogShape = RoundedCornerShape(28.dp)
    val DialogMaxWidth = 342.dp
    val DialogPadding = 16.dp
    val HeaderBottomPadding = 0.dp
    val ItemVerticalPadding = 8.dp
    val ItemBottomPaddingWithDivider = 17.dp
    val ItemContentSpacing = 16.dp
    val ItemDividerThickness = 1.dp
    val CloseButtonShape = RoundedCornerShape(999.dp)
    val CloseButtonContentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)

    val DialogBackground = defaultAisolverColorScheme.background.primary
    val ItemDividerColor = defaultAisolverColorScheme.stroke.border.copy(alpha = 0.35f)
    val TitleColor = defaultAisolverColorScheme.text.primary
    val DescriptionColor = defaultAisolverColorScheme.text.tertiary
    val CloseButtonForeground = defaultAisolverColorScheme.button.primaryBackground
}

data class AisolverSettingsItemModel(
    val title: String,
    val description: String,
    val checked: Boolean,
)

@Composable
fun AisolverSettingsDialog(
    spawnTileItem: AisolverSettingsItemModel,
    autoAnalyzeItem: AisolverSettingsItemModel,
    animationsItem: AisolverSettingsItemModel,
    onDismissRequest: () -> Unit,
    onSpawnTileCheckedChange: (Boolean) -> Unit,
    onAutoAnalyzeCheckedChange: (Boolean) -> Unit,
    onAnimationsCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .fillMaxWidth()
            .padding(AisolverSettingsDialogDefaults.DialogPadding)
            .widthIn(max = AisolverSettingsDialogDefaults.DialogMaxWidth),
        shape = AisolverSettingsDialogDefaults.DialogShape,
        containerColor = AisolverSettingsDialogDefaults.DialogBackground,
        title = {
            Text(
                text = "Settings",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AisolverSettingsDialogDefaults.HeaderBottomPadding),
                color = AisolverSettingsDialogDefaults.TitleColor,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    lineHeight = 32.sp,
                ),
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                AisolverSettingsItem(
                    item = spawnTileItem,
                    showDivider = true,
                    onCheckedChange = onSpawnTileCheckedChange,
                )
                AisolverSettingsItem(
                    item = autoAnalyzeItem,
                    showDivider = true,
                    onCheckedChange = onAutoAnalyzeCheckedChange,
                )
                AisolverSettingsItem(
                    item = animationsItem,
                    showDivider = true,
                    onCheckedChange = onAnimationsCheckedChange,
                )
                LinksPanel()
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                shape = AisolverSettingsDialogDefaults.CloseButtonShape,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = AisolverSettingsDialogDefaults.CloseButtonForeground,
                ),
                contentPadding = AisolverSettingsDialogDefaults.CloseButtonContentPadding,
            ) {
                Text(
                    text = "OK",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                    ),
                )
            }
        },
    )
}

@Composable
private fun AisolverSettingsItem(
    item: AisolverSettingsItemModel,
    showDivider: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = AisolverSettingsDialogDefaults.ItemVerticalPadding,
                    bottom = if (showDivider) {
                        AisolverSettingsDialogDefaults.ItemBottomPaddingWithDivider
                    } else {
                        AisolverSettingsDialogDefaults.ItemVerticalPadding
                    },
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AisolverSettingsDialogDefaults.ItemContentSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.title,
                    modifier = Modifier.weight(1f),
                    color = AisolverSettingsDialogDefaults.TitleColor,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                    ),
                )
                Switch(
                    checked = item.checked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialSwitchTrackColor,
                        checkedBorderColor = MaterialSwitchTrackColor,
                    ),
                )
            }

            Text(
                text = item.description,
                color = AisolverSettingsDialogDefaults.DescriptionColor,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                ),
            )
        }

        if (showDivider) {
            HorizontalDivider(
                thickness = AisolverSettingsDialogDefaults.ItemDividerThickness,
                color = AisolverSettingsDialogDefaults.ItemDividerColor,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LinksPanel() {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        val uriHandler = LocalUriHandler.current
        AisolverTextButton(
            onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) },
        ) {
            Text(text = stringResource(AisolverString.privacy_policy))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2F2F2F)
@Composable
private fun AisolverSettingsDialogPreview() {
    MaterialTheme {
        AisolverSettingsDialog(
            spawnTileItem = AisolverSettingsItemModel(
                title = "Spawn Tile",
                description = "After each valid move, add a new tile to an empty cell like the real game.",
                checked = true,
            ),
            autoAnalyzeItem = AisolverSettingsItemModel(
                title = "Auto Analyze",
                description = "Automatically run analysis whenever the board state changes.",
                checked = true,
            ),
            animationsItem = AisolverSettingsItemModel(
                title = "Animations",
                description = "Animate board moves and recommendation updates.",
                checked = true,
            ),
            onDismissRequest = {},
            onSpawnTileCheckedChange = {},
            onAutoAnalyzeCheckedChange = {},
            onAnimationsCheckedChange = {},
        )
    }
}

private const val PRIVACY_POLICY_URL = "https://policies.google.com/privacy"