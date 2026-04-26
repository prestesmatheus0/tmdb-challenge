package com.ifood.challenge.movies.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ifood.challenge.movies.core.designsystem.R
import com.ifood.challenge.movies.core.designsystem.preview.PreviewThemes
import com.ifood.challenge.movies.core.designsystem.theme.Dimens
import com.ifood.challenge.movies.core.designsystem.theme.IfoodMoviesTheme
import com.ifood.challenge.movies.core.designsystem.theme.spacing

@Composable
fun OfflineBanner(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm)
                .testTag(OfflineBannerTestTags.root),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
    ) {
        Icon(
            imageVector = Icons.Filled.CloudOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.size(Dimens.IconSizeMd),
        )
        Text(
            text = stringResource(R.string.offline_banner_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
        )
    }
}

object OfflineBannerTestTags {
    const val root = "offline_banner"
}

@PreviewThemes
@Composable
private fun OfflineBannerPreview() {
    IfoodMoviesTheme {
        OfflineBanner()
    }
}
