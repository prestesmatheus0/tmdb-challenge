package com.ifood.challenge.movies.domain.movies.usecase

import androidx.paging.PagingData
import com.ifood.challenge.movies.domain.movies.model.Movie
import kotlinx.coroutines.flow.Flow

fun interface GetPopularMoviesUseCase {
    operator fun invoke(): Flow<PagingData<Movie>>
}
