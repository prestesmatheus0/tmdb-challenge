package com.ifood.challenge.movies.domain.movies.internal

import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import com.ifood.challenge.movies.domain.movies.usecase.FetchMovieDetailUseCase

internal class FetchMovieDetailUseCaseImpl(
    private val repository: MoviesRepository,
) : FetchMovieDetailUseCase {
    override suspend operator fun invoke(movieId: Int) =
        repository.fetchAndCacheDetail(movieId)
}
