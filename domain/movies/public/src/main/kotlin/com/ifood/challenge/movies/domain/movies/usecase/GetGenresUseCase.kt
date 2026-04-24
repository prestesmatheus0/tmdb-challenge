package com.ifood.challenge.movies.domain.movies.usecase

import com.ifood.challenge.movies.domain.movies.model.Genre

fun interface GetGenresUseCase {
    suspend operator fun invoke(): List<Genre>
}
