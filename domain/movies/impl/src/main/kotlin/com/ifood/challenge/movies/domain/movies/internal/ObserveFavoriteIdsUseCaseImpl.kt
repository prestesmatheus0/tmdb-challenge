package com.ifood.challenge.movies.domain.movies.internal

import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import com.ifood.challenge.movies.domain.movies.usecase.ObserveFavoriteIdsUseCase
import kotlinx.coroutines.flow.Flow

internal class ObserveFavoriteIdsUseCaseImpl(
    private val repository: MoviesRepository,
) : ObserveFavoriteIdsUseCase {
    override operator fun invoke(): Flow<Set<Int>> = repository.observeAllFavoriteIds()
}
