package com.ifood.challenge.movies.domain.movies.internal

import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import com.ifood.challenge.movies.domain.movies.usecase.GetIsFavoriteUseCase
import kotlinx.coroutines.flow.Flow

internal class GetIsFavoriteUseCaseImpl(
    private val repository: MoviesRepository,
) : GetIsFavoriteUseCase {
    override operator fun invoke(movieId: Int): Flow<Boolean> =
        repository.observeIsFavorite(movieId)
}
