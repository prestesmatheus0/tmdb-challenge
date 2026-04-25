package com.ifood.challenge.movies.domain.movies.internal

import androidx.paging.PagingData
import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import com.ifood.challenge.movies.domain.movies.usecase.GetNowPlayingMoviesUseCase
import kotlinx.coroutines.flow.Flow

internal class GetNowPlayingMoviesUseCaseImpl(
    private val repository: MoviesRepository,
) : GetNowPlayingMoviesUseCase {
    override operator fun invoke(): Flow<PagingData<Movie>> = repository.nowPlayingPagingFlow()
}
