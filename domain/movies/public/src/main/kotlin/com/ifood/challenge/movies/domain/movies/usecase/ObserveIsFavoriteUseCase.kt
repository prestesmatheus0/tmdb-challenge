package com.ifood.challenge.movies.domain.movies.usecase

import kotlinx.coroutines.flow.Flow

fun interface ObserveIsFavoriteUseCase {
    operator fun invoke(movieId: Int): Flow<Boolean>
}
