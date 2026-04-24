package com.ifood.challenge.movies.domain.movies.internal

import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import com.ifood.challenge.movies.domain.movies.usecase.SetFavoriteUseCase

internal class SetFavoriteUseCaseImpl(
    private val repository: MoviesRepository,
) : SetFavoriteUseCase {
    override suspend operator fun invoke(movie: Movie, isFavorite: Boolean) =
        repository.setFavorite(movie, isFavorite)
}
