package com.ifood.challenge.movies.domain.movies.usecase

import kotlinx.coroutines.flow.Flow

fun interface ObserveFavoriteIdsUseCase {
    operator fun invoke(): Flow<Set<Int>>
}
