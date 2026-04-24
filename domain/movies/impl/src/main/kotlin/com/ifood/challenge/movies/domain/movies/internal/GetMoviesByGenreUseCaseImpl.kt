package com.ifood.challenge.movies.domain.movies.internal

import androidx.paging.PagingData
import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import com.ifood.challenge.movies.domain.movies.usecase.GetMoviesByGenreUseCase
import kotlinx.coroutines.flow.Flow

internal class GetMoviesByGenreUseCaseImpl(
    private val repository: MoviesRepository,
) : GetMoviesByGenreUseCase {
    override operator fun invoke(genreId: Int): Flow<PagingData<Movie>> =
        repository.discoverByGenrePagingFlow(genreId)
}
