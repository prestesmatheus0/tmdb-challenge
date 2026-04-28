package com.ifood.challenge.movies.feature.home.internal

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.ifood.challenge.movies.feature.home.HomeRoute
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.homeScreen(onMovieClick: (movieId: Int) -> Unit) {
    composable<HomeRoute> {
        val viewModel: HomeViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val movies = viewModel.moviesPagingFlow.collectAsLazyPagingItems()

        LaunchedEffect(viewModel) {
            viewModel.onViewCreated()
        }

        LaunchedEffect(viewModel.shuffleEvent) {
            viewModel.shuffleEvent.collect { movieId -> onMovieClick(movieId) }
        }

        HomeScreen(
            uiState = uiState,
            movies = movies,
            onMovieClick = onMovieClick,
            onAction = viewModel::onAction,
        )
    }
}
