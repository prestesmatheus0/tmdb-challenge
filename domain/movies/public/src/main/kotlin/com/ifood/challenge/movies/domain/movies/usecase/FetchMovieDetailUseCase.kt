package com.ifood.challenge.movies.domain.movies.usecase

fun interface FetchMovieDetailUseCase {
    suspend operator fun invoke(movieId: Int)
}
