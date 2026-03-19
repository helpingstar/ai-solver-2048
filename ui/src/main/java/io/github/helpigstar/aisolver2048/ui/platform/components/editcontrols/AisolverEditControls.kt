package io.github.helpigstar.aisolver2048.ui.platform.components.editcontrols

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.actionbutton.AisolverActionButton
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme

@Composable
fun AisolverEditControls(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EditButton(text = "Clear", compact = compact, modifier = Modifier.weight(1f))
            EditButton(text = "2", compact = compact, modifier = Modifier.weight(1f))
            EditButton(text = "4", compact = compact, modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EditButton(text = "x2", compact = compact, modifier = Modifier.weight(1f))
            EditButton(text = "÷2", compact = compact, modifier = Modifier.weight(1f))
            EditButton(text = "More", compact = compact, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun EditButton(
    text: String,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    AisolverActionButton(
        text = text,
        onClick = {},
        compact = compact,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun AisolverEditControls_preview() {
    AisolverPreviewTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AisolverEditControls()
            AisolverEditControls(compact = true)
        }
    }
}
