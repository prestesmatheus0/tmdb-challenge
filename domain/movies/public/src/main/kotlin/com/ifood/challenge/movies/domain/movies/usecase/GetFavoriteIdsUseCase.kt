package com.ifood.challenge.movies.domain.movies.usecase

import kotlinx.coroutines.flow.Flow

fun interface GetFavoriteIdsUseCase {
    operator fun invoke(): Flow<Set<Int>>
}
