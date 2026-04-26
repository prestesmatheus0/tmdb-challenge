package com.ifood.challenge.movies.feature.detail.internal

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ifood.challenge.movies.feature.detail.DetailRoute
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.detailScreen(onBack: () -> Unit) {
    composable<DetailRoute> {
        val viewModel: DetailViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        DetailScreen(
            uiState = uiState,
            onBack = onBack,
            onFavoriteToggle = viewModel::onFavoriteToggle,
            onRetry = viewModel::onRetry,
        )
    }
}
