package com.ifood.challenge.movies.domain.movies.internal

import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import com.ifood.challenge.movies.domain.movies.usecase.GetFavoriteIdsUseCase
import kotlinx.coroutines.flow.Flow

internal class GetFavoriteIdsUseCaseImpl(
    private val repository: MoviesRepository,
) : GetFavoriteIdsUseCase {
    override operator fun invoke(): Flow<Set<Int>> = repository.observeAllFavoriteIds()
}
