package com.ifood.challenge.movies.feature.home.internal

import com.ifood.challenge.movies.domain.movies.model.Genre

data class HomeUiState(
    val genres: List<Genre> = emptyList(),
    val selectedGenreId: Int? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val favoriteIds: Set<Int> = emptySet(),
    val isOffline: Boolean = false,
    val genresError: Boolean = false,
)
