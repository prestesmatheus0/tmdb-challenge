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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ifood.challenge.movies.core.designsystem.R
import com.ifood.challenge.movies.core.designsystem.preview.PreviewThemes
import com.ifood.challenge.movies.core.designsystem.theme.Dimens
import com.ifood.challenge.movies.core.designsystem.theme.IfoodMoviesTheme
import com.ifood.challenge.movies.core.designsystem.theme.spacing

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
                .padding(horizontal = MaterialTheme.spacing.xl, vertical = MaterialTheme.spacing.xxl)
                .testTag(ErrorStateTestTags.forVariant(variant)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(Dimens.CircleBackgroundLg)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = content.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(Dimens.IconSizeLg),
            )
        }
        Box(Modifier.size(MaterialTheme.spacing.md))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Box(Modifier.size(MaterialTheme.spacing.xs))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Box(Modifier.size(MaterialTheme.spacing.lg))
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

private class ErrorVariantParam : PreviewParameterProvider<ErrorVariant> {
    override val values = ErrorVariant.values().asSequence()
}

@PreviewThemes
@Composable
private fun ErrorStatePreview(
    @PreviewParameter(ErrorVariantParam::class) variant: ErrorVariant,
) {
    IfoodMoviesTheme {
        ErrorState(variant = variant, onRetry = {})
    }
}
