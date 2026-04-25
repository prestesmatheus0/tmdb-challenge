package com.ifood.challenge.movies.data.movies

import app.cash.turbine.test
import com.ifood.challenge.movies.core.database.dao.FavoriteDao
import com.ifood.challenge.movies.core.database.dao.MovieDao
import com.ifood.challenge.movies.core.database.dao.MovieDetailDao
import com.ifood.challenge.movies.core.database.entity.FavoriteEntity
import com.ifood.challenge.movies.core.database.entity.MovieDetailEntity
import com.ifood.challenge.movies.core.database.internal.MoviesDatabase
import com.ifood.challenge.movies.data.movies.internal.api.TmdbApiService
import com.ifood.challenge.movies.data.movies.internal.api.dto.GenreDto
import com.ifood.challenge.movies.data.movies.internal.api.dto.GenreListResponseDto
import com.ifood.challenge.movies.data.movies.internal.api.dto.MovieDetailDto
import com.ifood.challenge.movies.core.testing.TestDispatcherProvider
import com.ifood.challenge.movies.data.movies.internal.repository.MoviesRepositoryImpl
import com.ifood.challenge.movies.domain.movies.model.Movie
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

class MoviesRepositoryImplTest {
    private val apiService = mockk<TmdbApiService>()
    private val db = mockk<MoviesDatabase>()
    private val movieDao = mockk<MovieDao>()
    private val movieDetailDao = mockk<MovieDetailDao>()
    private val favoriteDao = mockk<FavoriteDao>()

    private val repository = MoviesRepositoryImpl(
        apiService = apiService,
        db = db,
        movieDao = movieDao,
        movieDetailDao = movieDetailDao,
        favoriteDao = favoriteDao,
        dispatchers = TestDispatcherProvider(),
    )

    private val movie = Movie(
        id = 1,
        title = "Inception",
        posterPath = "/p.jpg",
        backdropPath = null,
        overview = "A thief",
        voteAverage = 8.8,
        releaseDate = "2010-07-16",
        popularity = 100.0,
    )

    @Test
    fun `observeIsFavorite emite true quando favorito existe`() = runTest {
        every { favoriteDao.observeIsFavorite(1) } returns flowOf(true)

        repository.observeIsFavorite(1).test {
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `observeIsFavorite emite false quando nao e favorito`() = runTest {
        every { favoriteDao.observeIsFavorite(99) } returns flowOf(false)

        repository.observeIsFavorite(99).test {
            assertFalse(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `setFavorite true chama upsert no dao`() = runTest {
        coEvery { favoriteDao.upsert(any()) } returns Unit

        repository.setFavorite(movie, isFavorite = true)

        coVerify { favoriteDao.upsert(match { it.movieId == 1 && it.title == "Inception" }) }
    }

    @Test
    fun `setFavorite false chama delete no dao`() = runTest {
        coEvery { favoriteDao.delete(1) } returns Unit

        repository.setFavorite(movie, isFavorite = false)

        coVerify { favoriteDao.delete(1) }
    }

    @Test
    fun `fetchGenres retorna lista de generos mapeados`() = runTest {
        coEvery { apiService.genres() } returns GenreListResponseDto(
            genres = listOf(GenreDto(28, "Action"), GenreDto(12, "Adventure")),
        )

        val genres = repository.fetchGenres()

        assertEquals(2, genres.size)
        assertEquals("Action", genres[0].name)
        assertEquals(12, genres[1].id)
    }

    @Test
    fun `fetchAndCacheDetail chama api e faz upsert no dao`() = runTest {
        val dto = MovieDetailDto(id = 1, title = "Inception")
        coEvery { apiService.movieDetail(1) } returns dto
        coEvery { movieDetailDao.upsert(any()) } returns Unit

        repository.fetchAndCacheDetail(1)

        coVerify { apiService.movieDetail(1) }
        coVerify { movieDetailDao.upsert(match { it.id == 1 }) }
    }

    @Test
    fun `observeDetail emite null quando nao ha cache`() = runTest {
        every { movieDetailDao.observe(1) } returns flowOf(null)

        repository.observeDetail(1).test {
            assertEquals(null, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `observeDetail emite MovieDetail mapeado quando cache existe`() = runTest {
        val entity = MovieDetailEntity(
            id = 1, title = "Inception", posterPath = null, backdropPath = null,
            overview = "A thief", voteAverage = 8.8, releaseDate = "2010-07-16",
            runtimeMinutes = 148, tagline = "Your mind is the scene", genresCsv = "",
            fetchedAt = 0L,
        )
        every { movieDetailDao.observe(1) } returns flowOf(entity)

        repository.observeDetail(1).test {
            val detail = awaitItem()
            assertEquals(1, detail?.id)
            assertEquals("Inception", detail?.title)
            awaitComplete()
        }
    }
}
