package com.ifood.challenge.movies.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SignalWifiConnectedNoInternet4
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

enum class ErrorVariant { Network, Generic, Timeout, Server }

@Composable
fun ErrorState(
    variant: ErrorVariant,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (icon, title, description) = errorContent(variant)
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp)
                .testTag(ErrorStateTestTags.forVariant(variant)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(36.dp),
            )
        }
        Box(Modifier.size(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Box(Modifier.size(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Box(Modifier.size(24.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.testTag(ErrorStateTestTags.retry),
        ) {
            Text("Tentar novamente")
        }
    }
}

private data class ErrorContent(val icon: ImageVector, val title: String, val description: String)

private fun errorContent(variant: ErrorVariant): ErrorContent =
    when (variant) {
        ErrorVariant.Network ->
            ErrorContent(
                icon = Icons.Filled.SignalWifiConnectedNoInternet4,
                title = "Sem conexão",
                description = "Verifique sua internet e tente novamente.",
            )
        ErrorVariant.Timeout ->
            ErrorContent(
                icon = Icons.Filled.Schedule,
                title = "Tempo esgotado",
                description = "A requisição demorou demais. Tente novamente.",
            )
        ErrorVariant.Server ->
            ErrorContent(
                icon = Icons.Filled.CloudOff,
                title = "Servidor indisponível",
                description = "Estamos com instabilidade. Tente novamente em instantes.",
            )
        ErrorVariant.Generic ->
            ErrorContent(
                icon = Icons.Filled.Error,
                title = "Algo deu errado",
                description = "Não conseguimos carregar os filmes.",
            )
    }

object ErrorStateTestTags {
    const val retry = "error_state_retry"

    fun forVariant(variant: ErrorVariant) = "error_state_${variant.name.lowercase()}"
}
