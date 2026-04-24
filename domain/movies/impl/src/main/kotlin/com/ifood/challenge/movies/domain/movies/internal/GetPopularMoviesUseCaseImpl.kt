package com.ifood.challenge.movies.domain.movies.internal

import androidx.paging.PagingData
import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import com.ifood.challenge.movies.domain.movies.usecase.GetPopularMoviesUseCase
import kotlinx.coroutines.flow.Flow

internal class GetPopularMoviesUseCaseImpl(
    private val repository: MoviesRepository,
) : GetPopularMoviesUseCase {
    override operator fun invoke(): Flow<PagingData<Movie>> = repository.popularPagingFlow()
}
