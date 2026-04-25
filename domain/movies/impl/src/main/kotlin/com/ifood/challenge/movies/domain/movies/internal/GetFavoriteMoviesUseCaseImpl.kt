package com.ifood.challenge.movies.domain.movies.internal

import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import com.ifood.challenge.movies.domain.movies.usecase.GetFavoriteMoviesUseCase

internal class GetFavoriteMoviesUseCaseImpl(
    private val repository: MoviesRepository,
) : GetFavoriteMoviesUseCase {
    override fun invoke() = repository.observeFavoriteMovies()
}
