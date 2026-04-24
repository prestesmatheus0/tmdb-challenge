package com.ifood.challenge.movies.domain.movies.internal

import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import com.ifood.challenge.movies.domain.movies.usecase.ObserveIsFavoriteUseCase
import kotlinx.coroutines.flow.Flow

internal class ObserveIsFavoriteUseCaseImpl(
    private val repository: MoviesRepository,
) : ObserveIsFavoriteUseCase {
    override operator fun invoke(movieId: Int): Flow<Boolean> =
        repository.observeIsFavorite(movieId)
}
