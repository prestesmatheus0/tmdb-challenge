package com.ifood.challenge.movies.feature.detail.internal

import com.ifood.challenge.movies.domain.movies.model.MovieDetail

internal data class DetailUiState(
    val detail: MovieDetail? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
    val error: Boolean = false,
)
