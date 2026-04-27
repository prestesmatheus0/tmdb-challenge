package com.ifood.challenge.movies.core.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

object MotionTokens {
    val EmphasizedEasing: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val FavoriteSpringEasing: Easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)

    const val FavoriteAnimDurationMs = 280
    const val ScreenTransitionDurationMs = 220
    const val ShimmerDurationMs = 1600
}
