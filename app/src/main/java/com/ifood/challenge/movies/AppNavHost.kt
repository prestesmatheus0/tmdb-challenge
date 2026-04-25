package com.ifood.challenge.movies

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ifood.challenge.movies.core.designsystem.theme.MotionTokens
import com.ifood.challenge.movies.feature.detail.DetailRoute
import com.ifood.challenge.movies.feature.detail.internal.detailScreen
import com.ifood.challenge.movies.feature.home.HomeRoute
import com.ifood.challenge.movies.feature.home.internal.homeScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
        enterTransition = {
            fadeIn(tween(MotionTokens.ScreenTransitionDurationMs)) +
                slideInHorizontally(tween(MotionTokens.ScreenTransitionDurationMs)) { it / 12 }
        },
        exitTransition = {
            fadeOut(tween(MotionTokens.ScreenTransitionDurationMs)) +
                slideOutHorizontally(tween(MotionTokens.ScreenTransitionDurationMs)) { -it / 12 }
        },
        popEnterTransition = {
            fadeIn(tween(MotionTokens.ScreenTransitionDurationMs)) +
                slideInHorizontally(tween(MotionTokens.ScreenTransitionDurationMs)) { -it / 12 }
        },
        popExitTransition = {
            fadeOut(tween(MotionTokens.ScreenTransitionDurationMs)) +
                slideOutHorizontally(tween(MotionTokens.ScreenTransitionDurationMs)) { it / 12 }
        },
    ) {
        homeScreen(
            onMovieClick = { movieId -> navController.navigate(DetailRoute(movieId)) },
        )
        detailScreen(
            onBack = { navController.popBackStack() },
        )
    }
}
