package com.ifood.challenge.movies.domain.movies

import app.cash.turbine.test
import com.ifood.challenge.movies.domain.movies.internal.FetchMovieDetailUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetGenresUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.ObserveIsFavoriteUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.ObserveMovieDetailUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.SetFavoriteUseCaseImpl
import com.ifood.challenge.movies.domain.movies.model.Genre
import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.domain.movies.model.MovieDetail
import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UseCasesTest {
    private val repository = mockk<MoviesRepository>()

    private val movie = Movie(
        id = 1, title = "Inception", posterPath = null, backdropPath = null,
        overview = "", voteAverage = 8.8, releaseDate = null, popularity = 100.0,
    )

    // GetGenresUseCase

    @Test
    fun `GetGenresUseCase delega para repository fetchGenres`() = runTest {
        val genres = listOf(Genre(28, "Action"), Genre(12, "Adventure"))
        coEvery { repository.fetchGenres() } returns genres

        val result = GetGenresUseCaseImpl(repository)()

        assertEquals(genres, result)
        coVerify(exactly = 1) { repository.fetchGenres() }
    }

    // ObserveMovieDetailUseCase

    @Test
    fun `ObserveMovieDetailUseCase emite MovieDetail do repository`() = runTest {
        val detail = MovieDetail(
            id = 1, title = "Inception", posterPath = null, backdropPath = null,
            overview = "", voteAverage = 8.8, releaseDate = null,
            runtimeMinutes = 148, tagline = null, genres = emptyList(),
        )
        every { repository.observeDetail(1) } returns flowOf(detail)

        ObserveMovieDetailUseCaseImpl(repository)(1).test {
            assertEquals(detail, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `ObserveMovieDetailUseCase emite null quando sem cache`() = runTest {
        every { repository.observeDetail(99) } returns flowOf(null)

        ObserveMovieDetailUseCaseImpl(repository)(99).test {
            assertEquals(null, awaitItem())
            awaitComplete()
        }
    }

    // FetchMovieDetailUseCase

    @Test
    fun `FetchMovieDetailUseCase chama repository fetchAndCacheDetail`() = runTest {
        coEvery { repository.fetchAndCacheDetail(1) } returns Unit

        FetchMovieDetailUseCaseImpl(repository)(1)

        coVerify(exactly = 1) { repository.fetchAndCacheDetail(1) }
    }

    // ObserveIsFavoriteUseCase

    @Test
    fun `ObserveIsFavoriteUseCase emite true quando favorito`() = runTest {
        every { repository.observeIsFavorite(1) } returns flowOf(true)

        ObserveIsFavoriteUseCaseImpl(repository)(1).test {
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `ObserveIsFavoriteUseCase emite false quando nao e favorito`() = runTest {
        every { repository.observeIsFavorite(1) } returns flowOf(false)

        ObserveIsFavoriteUseCaseImpl(repository)(1).test {
            assertFalse(awaitItem())
            awaitComplete()
        }
    }

    // SetFavoriteUseCase

    @Test
    fun `SetFavoriteUseCase chama setFavorite com isFavorite true`() = runTest {
        coEvery { repository.setFavorite(movie, true) } returns Unit

        SetFavoriteUseCaseImpl(repository)(movie, isFavorite = true)

        coVerify(exactly = 1) { repository.setFavorite(movie, true) }
    }

    @Test
    fun `SetFavoriteUseCase chama setFavorite com isFavorite false`() = runTest {
        coEvery { repository.setFavorite(movie, false) } returns Unit

        SetFavoriteUseCaseImpl(repository)(movie, isFavorite = false)

        coVerify(exactly = 1) { repository.setFavorite(movie, false) }
    }
}
