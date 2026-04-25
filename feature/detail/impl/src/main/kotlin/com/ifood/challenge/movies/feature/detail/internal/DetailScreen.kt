package com.ifood.challenge.movies.feature.detail.internal

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ifood.challenge.movies.core.designsystem.component.ErrorState
import com.ifood.challenge.movies.core.designsystem.component.ErrorVariant
import com.ifood.challenge.movies.core.network.ImageUrlBuilder
import com.ifood.challenge.movies.domain.movies.model.MovieDetail
import org.koin.compose.koinInject

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DetailScreen(
    uiState: DetailUiState,
    onBack: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    imageUrlBuilder: ImageUrlBuilder = koinInject(),
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val collapseThresholdPx = remember(density) { with(density) { 300.dp.toPx() } }
    val collapseAlpha by remember(scrollState) {
        derivedStateOf { (scrollState.value / collapseThresholdPx).coerceIn(0f, 1f) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.error -> ErrorState(
                variant = ErrorVariant.Generic,
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize(),
            )
            uiState.isLoading && uiState.detail == null -> DetailSkeleton()
            uiState.detail != null -> DetailContent(
                detail = uiState.detail,
                isFavorite = uiState.isFavorite,
                onFavoriteToggle = onFavoriteToggle,
                imageUrlBuilder = imageUrlBuilder,
                scrollState = scrollState,
            )
        }

        CollapsingTopBar(
            title = uiState.detail?.title.orEmpty(),
            isFavorite = uiState.isFavorite,
            showFavorite = uiState.detail != null,
            collapseAlpha = collapseAlpha,
            onBack = onBack,
            onFavoriteToggle = onFavoriteToggle,
        )
    }
}

@Composable
private fun CollapsingTopBar(
    title: String,
    isFavorite: Boolean,
    showFavorite: Boolean,
    collapseAlpha: Float,
    onBack: () -> Unit,
    onFavoriteToggle: () -> Unit,
) {
    val circleAlpha = (1f - collapseAlpha) * 0.35f
    val iconTint = lerp(Color.White, MaterialTheme.colorScheme.onSurface, collapseAlpha)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = collapseAlpha))
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = circleAlpha)),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = iconTint,
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
                .alpha(collapseAlpha),
        )

        if (showFavorite) {
            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = circleAlpha)),
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                    tint = iconTint,
                )
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailContent(
    detail: MovieDetail,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    imageUrlBuilder: ImageUrlBuilder,
    scrollState: ScrollState,
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        Box(modifier = Modifier.fillMaxWidth().height(360.dp)) {
            AsyncImage(
                model = imageUrlBuilder.poster(detail.posterPath),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.55f), Color.Transparent),
                        ),
                    ),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surface),
                        ),
                    ),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 120.dp),
        ) {
            Text(
                text = detail.title,
                style = MaterialTheme.typography.headlineSmall,
            )

            val tagline = detail.tagline
            if (!tagline.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tagline,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                detail.releaseDate?.take(4)?.let { year ->
                    Text(text = year, style = MaterialTheme.typography.bodyMedium)
                }
                detail.runtimeMinutes?.let { runtime ->
                    Text(
                        text = "· ${runtime}min",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = " ${"%.1f".format(detail.voteAverage)}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            if (detail.genres.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    detail.genres.forEach { genre ->
                        AssistChip(
                            onClick = {},
                            label = { Text(genre.name) },
                            colors = AssistChipDefaults.assistChipColors(),
                        )
                    }
                }
            }

            if (detail.overview.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Sinopse",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = detail.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isFavorite) {
                OutlinedButton(
                    onClick = onFavoriteToggle,
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Text("Remover dos favoritos")
                }
            } else {
                Button(
                    onClick = onFavoriteToggle,
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Text("Adicionar aos favoritos")
                }
            }
        }
    }
}

@Composable
private fun DetailSkeleton() {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            repeat(5) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (it == 0) 0.7f else 1f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                )
            }
        }
    }
}
