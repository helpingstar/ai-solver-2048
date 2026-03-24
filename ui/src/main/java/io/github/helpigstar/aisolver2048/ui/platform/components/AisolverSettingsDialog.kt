package io.github.helpigstar.aisolver2048.ui.platform.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme

private val MaterialSwitchTrackColor = Color(0xFF6750A4)

object AisolverSettingsDialogDefaults {
    val DialogShape = RoundedCornerShape(24.dp)
    val ItemShape = RoundedCornerShape(14.dp)
    val DialogMaxWidth = 360.dp
    val DialogPadding = 20.dp
    val ContentPadding = 24.dp
    val ContentSpacing = 24.dp
    val ItemSpacing = 16.dp
    val ItemContentPadding = 16.dp
    val ItemTextSpacing = 4.dp
    val CloseButtonShape = RoundedCornerShape(14.dp)

    val DialogBackground = defaultAisolverColorScheme.background.primary
    val ItemBackground = defaultAisolverColorScheme.background.tertiary
    val ItemBorder = defaultAisolverColorScheme.stroke.border
    val TitleColor = defaultAisolverColorScheme.text.primary
    val DescriptionColor = defaultAisolverColorScheme.text.tertiary
    val CloseButtonBackground = defaultAisolverColorScheme.button.primaryBackground
    val CloseButtonForeground = defaultAisolverColorScheme.button.primaryForeground
}

data class AisolverSettingsItemModel(
    val title: String,
    val description: String,
    val checked: Boolean,
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AisolverSettingsDialog(
    spawnTileItem: AisolverSettingsItemModel,
    autoAnalyzeItem: AisolverSettingsItemModel,
    onDismissRequest: () -> Unit,
    onSpawnTileCheckedChange: (Boolean) -> Unit,
    onAutoAnalyzeCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AisolverSettingsDialogDefaults.DialogPadding)
                .widthIn(max = AisolverSettingsDialogDefaults.DialogMaxWidth),
            shape = AisolverSettingsDialogDefaults.DialogShape,
            color = AisolverSettingsDialogDefaults.DialogBackground,
            tonalElevation = 0.dp,
            shadowElevation = 20.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AisolverSettingsDialogDefaults.ContentPadding),
                verticalArrangement = Arrangement.spacedBy(AisolverSettingsDialogDefaults.ContentSpacing),
            ) {
                Text(
                    text = "Settings",
                    color = AisolverSettingsDialogDefaults.TitleColor,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                    ),
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(AisolverSettingsDialogDefaults.ItemSpacing),
                ) {
                    AisolverSettingsItem(
                        item = spawnTileItem,
                        onCheckedChange = onSpawnTileCheckedChange,
                    )
                    AisolverSettingsItem(
                        item = autoAnalyzeItem,
                        onCheckedChange = onAutoAnalyzeCheckedChange,
                    )
                }
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth(),
                    shape = AisolverSettingsDialogDefaults.CloseButtonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AisolverSettingsDialogDefaults.CloseButtonBackground,
                        contentColor = AisolverSettingsDialogDefaults.CloseButtonForeground,
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        focusedElevation = 0.dp,
                        hoveredElevation = 0.dp,
                        disabledElevation = 0.dp,
                    ),
                ) {
                    Text(
                        text = "Close",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun AisolverSettingsItem(
    item: AisolverSettingsItemModel,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = AisolverSettingsDialogDefaults.ItemShape,
        color = AisolverSettingsDialogDefaults.ItemBackground,
        border = BorderStroke(
            width = 1.dp,
            color = AisolverSettingsDialogDefaults.ItemBorder,
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AisolverSettingsDialogDefaults.ItemContentPadding),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AisolverSettingsDialogDefaults.ItemTextSpacing),
            ) {
                Text(
                    text = item.title,
                    color = AisolverSettingsDialogDefaults.TitleColor,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                    ),
                )
                Text(
                    text = item.description,
                    color = AisolverSettingsDialogDefaults.DescriptionColor,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                    ),
                )
            }
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
            onDismissRequest = {},
            onSpawnTileCheckedChange = {},
            onAutoAnalyzeCheckedChange = {},
        )
    }
}
