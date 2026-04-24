package com.ifood.challenge.movies

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
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
    ) {
        homeScreen(
            onMovieClick = { movieId -> navController.navigate(DetailRoute(movieId)) },
        )
        detailScreen(
            onBack = { navController.popBackStack() },
        )
    }
}
