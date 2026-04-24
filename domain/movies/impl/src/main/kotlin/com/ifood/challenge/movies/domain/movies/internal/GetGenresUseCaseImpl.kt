package com.ifood.challenge.movies.domain.movies.internal

import com.ifood.challenge.movies.domain.movies.model.Genre
import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import com.ifood.challenge.movies.domain.movies.usecase.GetGenresUseCase

internal class GetGenresUseCaseImpl(
    private val repository: MoviesRepository,
) : GetGenresUseCase {
    override suspend operator fun invoke(): List<Genre> = repository.fetchGenres()
}
