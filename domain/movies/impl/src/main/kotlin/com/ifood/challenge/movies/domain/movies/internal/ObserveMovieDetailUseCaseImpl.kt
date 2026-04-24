package com.ifood.challenge.movies.domain.movies.internal

import com.ifood.challenge.movies.domain.movies.model.MovieDetail
import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import com.ifood.challenge.movies.domain.movies.usecase.ObserveMovieDetailUseCase
import kotlinx.coroutines.flow.Flow

internal class ObserveMovieDetailUseCaseImpl(
    private val repository: MoviesRepository,
) : ObserveMovieDetailUseCase {
    override operator fun invoke(movieId: Int): Flow<MovieDetail?> =
        repository.observeDetail(movieId)
}
