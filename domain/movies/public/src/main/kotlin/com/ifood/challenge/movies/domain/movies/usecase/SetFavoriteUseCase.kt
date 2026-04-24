package com.ifood.challenge.movies.domain.movies.usecase

import com.ifood.challenge.movies.domain.movies.model.Movie

fun interface SetFavoriteUseCase {
    suspend operator fun invoke(movie: Movie, isFavorite: Boolean)
}
