package com.ifood.challenge.movies.feature.home.internal

internal sealed interface HomeFilter {
    data object Popular : HomeFilter
    data class Genre(val genreId: Int) : HomeFilter
    data object Favorites : HomeFilter
}
