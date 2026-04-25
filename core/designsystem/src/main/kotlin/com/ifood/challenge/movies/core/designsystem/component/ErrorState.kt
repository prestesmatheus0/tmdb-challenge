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
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ifood.challenge.movies.core.designsystem.R

enum class ErrorVariant { Network, Generic, Timeout, Server }

@Composable
fun ErrorState(
    variant: ErrorVariant,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val content = errorContent(variant)
    val title = stringResource(content.titleRes)
    val description = stringResource(content.descriptionRes)
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
                imageVector = content.icon,
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
            Text(stringResource(R.string.error_retry))
        }
    }
}

private data class ErrorContent(
    val icon: ImageVector,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
)

private fun errorContent(variant: ErrorVariant): ErrorContent =
    when (variant) {
        ErrorVariant.Network ->
            ErrorContent(
                icon = Icons.Filled.SignalWifiConnectedNoInternet4,
                titleRes = R.string.error_network_title,
                descriptionRes = R.string.error_network_description,
            )
        ErrorVariant.Timeout ->
            ErrorContent(
                icon = Icons.Filled.Schedule,
                titleRes = R.string.error_timeout_title,
                descriptionRes = R.string.error_timeout_description,
            )
        ErrorVariant.Server ->
            ErrorContent(
                icon = Icons.Filled.CloudOff,
                titleRes = R.string.error_server_title,
                descriptionRes = R.string.error_server_description,
            )
        ErrorVariant.Generic ->
            ErrorContent(
                icon = Icons.Filled.Error,
                titleRes = R.string.error_generic_title,
                descriptionRes = R.string.error_generic_description,
            )
    }

object ErrorStateTestTags {
    const val retry = "error_state_retry"

    fun forVariant(variant: ErrorVariant) = "error_state_${variant.name.lowercase()}"
}
