package com.ifood.challenge.movies.core.designsystem.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.ifood.challenge.movies.core.designsystem.theme.MotionTokens

@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(MotionTokens.ShimmerDurationMs),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "shimmer-offset",
        )

    val base = MaterialTheme.colorScheme.surfaceContainerHigh
    val highlight = MaterialTheme.colorScheme.surfaceContainerHighest

    Box(
        modifier =
            modifier
                .background(
                    brush =
                        Brush.linearGradient(
                            colors = listOf(base, highlight, base),
                            start = androidx.compose.ui.geometry.Offset(translate - 500f, 0f),
                            end = androidx.compose.ui.geometry.Offset(translate, 0f),
                        ),
                ),
    )
}

@Composable
fun MovieCardSkeleton(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
    ) {
        Column {
            ShimmerBox(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f),
            )
            Column(Modifier.padding(12.dp)) {
                ShimmerBox(
                    modifier =
                        Modifier
                            .fillMaxWidth(0.8f)
                            .height(16.dp)
                            .clip(MaterialTheme.shapes.extraSmall),
                )
                Box(Modifier.height(6.dp))
                ShimmerBox(
                    modifier =
                        Modifier
                            .fillMaxWidth(0.4f)
                            .height(12.dp)
                            .clip(MaterialTheme.shapes.extraSmall),
                )
            }
        }
    }
}
