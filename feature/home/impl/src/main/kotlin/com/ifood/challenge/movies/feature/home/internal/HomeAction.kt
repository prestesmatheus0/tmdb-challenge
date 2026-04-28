package com.ifood.challenge.movies.feature.home.internal

import com.ifood.challenge.movies.domain.movies.model.Movie

internal sealed interface HomeAction {
    data class FilterSelect(val filter: HomeFilter) : HomeAction

    data class SearchQueryChange(val query: String) : HomeAction

    data object SearchToggle : HomeAction

    data class FavoriteToggle(val movie: Movie) : HomeAction

    data object Shuffle : HomeAction
}
