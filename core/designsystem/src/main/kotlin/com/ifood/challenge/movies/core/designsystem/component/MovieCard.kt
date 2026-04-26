package com.ifood.challenge.movies.core.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ifood.challenge.movies.core.designsystem.R
import com.ifood.challenge.movies.core.designsystem.theme.Dimens
import com.ifood.challenge.movies.core.designsystem.theme.MotionTokens
import com.ifood.challenge.movies.core.designsystem.theme.spacing

@Composable
fun MovieCard(
    title: String,
    posterUrl: String?,
    rating: Double,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .testTag(MovieCardTestTags.root(title)),
        shape = MaterialTheme.shapes.medium,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
    ) {
        Column {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                if (posterUrl != null) {
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                FavoriteButton(
                    title = title,
                    isFavorite = isFavorite,
                    onToggle = onFavoriteToggle,
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(MaterialTheme.spacing.xs),
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.sm),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                RatingRow(rating = rating)
            }
        }
    }
}

@Composable
private fun FavoriteButton(
    title: String,
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isFavorite) {
        if (isFavorite) {
            scale.animateTo(
                targetValue = 1.25f,
                animationSpec =
                    tween(
                        durationMillis = MotionTokens.FavoriteAnimDurationMs / 2,
                        easing = MotionTokens.FavoriteSpringEasing,
                    ),
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec =
                    tween(
                        durationMillis = MotionTokens.FavoriteAnimDurationMs / 2,
                        easing = MotionTokens.FavoriteSpringEasing,
                    ),
            )
        }
    }

    IconButton(
        onClick = onToggle,
        modifier =
            modifier
                .size(Dimens.FavoriteButtonSize)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(Color.Black.copy(alpha = 0.35f))
                .scale(scale.value)
                .semantics {
                    toggleableState =
                        if (isFavorite) ToggleableState.On else ToggleableState.Off
                }
                .testTag(MovieCardTestTags.favorite(title)),
    ) {
        Icon(
            imageVector =
                if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription =
                if (isFavorite) {
                    stringResource(R.string.movie_card_remove_favorite, title)
                } else {
                    stringResource(R.string.movie_card_add_favorite, title)
                },
            tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.White,
        )
    }
}

@Composable
private fun RatingRow(rating: Double) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs),
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.IconSizeXs),
        )
        Text(
            text = "%.1f".format(rating),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

object MovieCardTestTags {
    fun root(title: String) = "movie_card_$title"

    fun favorite(title: String) = "movie_card_favorite_$title"
}
