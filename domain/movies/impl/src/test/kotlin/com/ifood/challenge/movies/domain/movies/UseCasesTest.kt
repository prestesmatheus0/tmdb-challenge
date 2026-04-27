package com.ifood.challenge.movies.domain.movies

import androidx.paging.PagingData
import app.cash.turbine.test
import com.ifood.challenge.movies.domain.movies.internal.FetchMovieDetailUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetFavoriteIdsUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetFavoriteMoviesUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetGenresUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetIsFavoriteUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetMovieDetailUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetMoviesByGenreUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetMoviesByQueryUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetNowPlayingMoviesUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetPopularMoviesUseCaseImpl
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
        id = 1,
        title = "Inception",
        posterPath = null,
        backdropPath = null,
        overview = "",
        voteAverage = 8.8,
        releaseDate = null,
        popularity = 100.0,
    )

    @Test
    fun `GetGenresUseCase delega para repository fetchGenres`() = runTest {
        val genres = listOf(Genre(28, "Action"), Genre(12, "Adventure"))
        coEvery { repository.fetchGenres() } returns genres

        val result = GetGenresUseCaseImpl(repository)()

        assertEquals(genres, result)
        coVerify(exactly = 1) { repository.fetchGenres() }
    }

    @Test
    fun `GetMovieDetailUseCase emite MovieDetail do repository`() = runTest {
        val detail = MovieDetail(
            id = 1, title = "Inception", posterPath = null, backdropPath = null,
            overview = "", voteAverage = 8.8, releaseDate = null,
            runtimeMinutes = 148, tagline = null, genres = emptyList(),
            popularity = 0.0,
        )
        every { repository.observeDetail(1) } returns flowOf(detail)

        GetMovieDetailUseCaseImpl(repository)(1).test {
            assertEquals(detail, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `GetMovieDetailUseCase emite null quando sem cache`() = runTest {
        every { repository.observeDetail(99) } returns flowOf(null)

        GetMovieDetailUseCaseImpl(repository)(99).test {
            assertEquals(null, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `FetchMovieDetailUseCase chama repository fetchAndCacheDetail`() = runTest {
        coEvery { repository.fetchAndCacheDetail(1) } returns Unit

        FetchMovieDetailUseCaseImpl(repository)(1)

        coVerify(exactly = 1) { repository.fetchAndCacheDetail(1) }
    }

    @Test
    fun `GetIsFavoriteUseCase emite true quando favorito`() = runTest {
        every { repository.observeIsFavorite(1) } returns flowOf(true)

        GetIsFavoriteUseCaseImpl(repository)(1).test {
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `GetIsFavoriteUseCase emite false quando nao e favorito`() = runTest {
        every { repository.observeIsFavorite(1) } returns flowOf(false)

        GetIsFavoriteUseCaseImpl(repository)(1).test {
            assertFalse(awaitItem())
            awaitComplete()
        }
    }

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

    @Test
    fun `GetPopularMoviesUseCase delega para repository popularPagingFlow`() = runTest {
        val expected = flowOf(PagingData.empty<Movie>())
        every { repository.popularPagingFlow() } returns expected

        val result = GetPopularMoviesUseCaseImpl(repository)()

        assertEquals(expected, result)
    }

    @Test
    fun `GetNowPlayingMoviesUseCase delega para repository nowPlayingPagingFlow`() = runTest {
        val expected = flowOf(PagingData.empty<Movie>())
        every { repository.nowPlayingPagingFlow() } returns expected

        val result = GetNowPlayingMoviesUseCaseImpl(repository)()

        assertEquals(expected, result)
    }

    @Test
    fun `GetMoviesByGenreUseCase delega com genreId`() = runTest {
        val expected = flowOf(PagingData.empty<Movie>())
        every { repository.discoverByGenrePagingFlow(28) } returns expected

        val result = GetMoviesByGenreUseCaseImpl(repository)(28)

        assertEquals(expected, result)
    }

    @Test
    fun `GetMoviesByQueryUseCase delega com query`() = runTest {
        val expected = flowOf(PagingData.empty<Movie>())
        every { repository.searchPagingFlow("inception") } returns expected

        val result = GetMoviesByQueryUseCaseImpl(repository)("inception")

        assertEquals(expected, result)
    }

    @Test
    fun `GetFavoriteMoviesUseCase emite lista do repository`() = runTest {
        val movies = listOf(movie)
        every { repository.observeFavoriteMovies() } returns flowOf(movies)

        GetFavoriteMoviesUseCaseImpl(repository)().test {
            assertEquals(movies, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `GetFavoriteIdsUseCase emite Set de ids do repository`() = runTest {
        val ids = setOf(1, 2, 3)
        every { repository.observeAllFavoriteIds() } returns flowOf(ids)

        GetFavoriteIdsUseCaseImpl(repository)().test {
            assertEquals(ids, awaitItem())
            awaitComplete()
        }
    }
}
