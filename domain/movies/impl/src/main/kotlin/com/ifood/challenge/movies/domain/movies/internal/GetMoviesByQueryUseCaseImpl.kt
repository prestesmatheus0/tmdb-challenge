package com.ifood.challenge.movies.domain.movies.internal

import androidx.paging.PagingData
import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import com.ifood.challenge.movies.domain.movies.usecase.GetMoviesByQueryUseCase
import kotlinx.coroutines.flow.Flow

internal class GetMoviesByQueryUseCaseImpl(
    private val repository: MoviesRepository,
) : GetMoviesByQueryUseCase {
    override operator fun invoke(query: String): Flow<PagingData<Movie>> =
        repository.searchPagingFlow(query)
}
