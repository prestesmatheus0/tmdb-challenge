package com.ifood.challenge.movies.domain.movies.usecase

import com.ifood.challenge.movies.domain.movies.model.Movie
import kotlinx.coroutines.flow.Flow

fun interface GetFavoriteMoviesUseCase {
    operator fun invoke(): Flow<List<Movie>>
}
