package com.ifood.challenge.movies.feature.detail.internal

internal sealed interface DetailAction {
    data object FavoriteToggle : DetailAction

    data object Retry : DetailAction
}
