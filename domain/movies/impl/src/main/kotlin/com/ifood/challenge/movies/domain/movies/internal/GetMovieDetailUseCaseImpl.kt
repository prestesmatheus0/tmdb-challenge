package com.ifood.challenge.movies.domain.movies.internal

import com.ifood.challenge.movies.domain.movies.model.MovieDetail
import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import com.ifood.challenge.movies.domain.movies.usecase.GetMovieDetailUseCase
import kotlinx.coroutines.flow.Flow

internal class GetMovieDetailUseCaseImpl(
    private val repository: MoviesRepository,
) : GetMovieDetailUseCase {
    override operator fun invoke(movieId: Int): Flow<MovieDetail?> =
        repository.observeDetail(movieId)
}
