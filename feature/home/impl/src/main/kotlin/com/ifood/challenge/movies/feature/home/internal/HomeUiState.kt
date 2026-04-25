package com.ifood.challenge.movies.feature.home.internal

import com.ifood.challenge.movies.domain.movies.model.Genre
import com.ifood.challenge.movies.domain.movies.model.Movie

internal data class HomeUiState(
    val genres: List<Genre> = emptyList(),
    val filter: HomeFilter = HomeFilter.Popular,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val favoriteIds: Set<Int> = emptySet(),
    val favoriteMovies: List<Movie> = emptyList(),
    val isOffline: Boolean = false,
    val genresError: Boolean = false,
)
