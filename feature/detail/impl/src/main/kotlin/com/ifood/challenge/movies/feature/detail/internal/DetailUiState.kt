package com.ifood.challenge.movies.feature.detail.internal

import com.ifood.challenge.movies.domain.movies.model.MovieDetail

internal sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Success(
        val detail: MovieDetail,
        val isFavorite: Boolean,
    ) : DetailUiState

    data object Error : DetailUiState
}
