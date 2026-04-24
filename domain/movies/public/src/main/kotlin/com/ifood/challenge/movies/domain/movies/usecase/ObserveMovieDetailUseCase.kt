package com.ifood.challenge.movies.domain.movies.usecase

import com.ifood.challenge.movies.domain.movies.model.MovieDetail
import kotlinx.coroutines.flow.Flow

fun interface ObserveMovieDetailUseCase {
    operator fun invoke(movieId: Int): Flow<MovieDetail?>
}
