package com.ifood.challenge.movies.feature.detail.internal

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ifood.challenge.movies.feature.detail.DetailRoute
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.detailScreen(onBack: () -> Unit) {
    composable<DetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<DetailRoute>()
        val viewModel: DetailViewModel = koinViewModel { parametersOf(route.movieId) }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        DetailScreen(
            uiState = uiState,
            onBack = onBack,
            onFavoriteToggle = viewModel::onFavoriteToggle,
        )
    }
}
