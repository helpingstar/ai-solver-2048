package io.github.helpigstar.aisolver2048.ui.onboarding.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Text(
            text = state.title,
            style = MaterialTheme.typography.headlineMedium,
        )

        Text(
            text = state.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 12.dp),
        )

        Button(
            onClick = {
                viewModel.trySendAction(WelcomeAction.StartClick)
            },
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
        ) {
            Text(text = state.buttonText)
        }
    }
}