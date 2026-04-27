package com.ifood.challenge.movies.data.movies

import app.cash.turbine.test
import com.ifood.challenge.movies.core.database.dao.FavoriteDao
import com.ifood.challenge.movies.core.database.dao.MovieDao
import com.ifood.challenge.movies.core.database.dao.MovieDetailDao
import com.ifood.challenge.movies.core.database.entity.FavoriteEntity
import com.ifood.challenge.movies.core.database.entity.MovieDetailEntity
import com.ifood.challenge.movies.core.database.internal.MoviesDatabase
import com.ifood.challenge.movies.core.testing.TestDispatcherProvider
import com.ifood.challenge.movies.data.movies.internal.api.TmdbApiService
import com.ifood.challenge.movies.data.movies.internal.api.dto.GenreDto
import com.ifood.challenge.movies.data.movies.internal.api.dto.GenreListResponseDto
import com.ifood.challenge.movies.data.movies.internal.api.dto.MovieDetailDto
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
    private val db = mockk<MoviesDatabase>(relaxed = true)
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
            runtimeMinutes = 148, tagline = "Your mind is the scene", popularity = 0.0, genresCsv = "",
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

    @Test
    fun `observeAllFavoriteIds extrai movieIds das entities`() = runTest {
        val entities = listOf(
            FavoriteEntity(
                movieId = 1, title = "A", posterPath = null, backdropPath = null,
                overview = "", voteAverage = 0.0, releaseDate = null, popularity = 0.0, addedAt = 0L,
            ),
            FavoriteEntity(
                movieId = 2, title = "B", posterPath = null, backdropPath = null,
                overview = "", voteAverage = 0.0, releaseDate = null, popularity = 0.0, addedAt = 0L,
            ),
        )
        every { favoriteDao.observeAll() } returns flowOf(entities)

        repository.observeAllFavoriteIds().test {
            assertEquals(setOf(1, 2), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `observeAllFavoriteIds emite set vazio quando dao retorna lista vazia`() = runTest {
        every { favoriteDao.observeAll() } returns flowOf(emptyList())

        repository.observeAllFavoriteIds().test {
            assertEquals(emptySet<Int>(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `observeFavoriteMovies mapeia FavoriteEntity para Movie completo`() = runTest {
        val entity = FavoriteEntity(
            movieId = 27205,
            title = "Inception",
            posterPath = "/p.jpg",
            backdropPath = "/bd.jpg",
            overview = "A thief who steals secrets",
            voteAverage = 8.8,
            releaseDate = "2010-07-16",
            popularity = 100.0,
            addedAt = 1L,
        )
        every { favoriteDao.observeAll() } returns flowOf(listOf(entity))

        repository.observeFavoriteMovies().test {
            val movies = awaitItem()
            assertEquals(1, movies.size)
            val m = movies[0]
            assertEquals(27205, m.id)
            assertEquals("Inception", m.title)
            assertEquals("/bd.jpg", m.backdropPath)
            assertEquals("A thief who steals secrets", m.overview)
            assertEquals(100.0, m.popularity, 0.0)
            awaitComplete()
        }
    }

    @Test
    fun `popularPagingFlow expoe Flow do Pager`() = runTest {
        every { movieDao.pagingSource() } returns mockk(relaxed = true)
        val flow = repository.popularPagingFlow()
        assertTrue(flow !== null)
    }

    @Test
    fun `nowPlayingPagingFlow expoe Flow do Pager`() = runTest {
        val flow = repository.nowPlayingPagingFlow()
        assertTrue(flow !== null)
    }

    @Test
    fun `discoverByGenrePagingFlow expoe Flow do Pager para genreId`() = runTest {
        val flow = repository.discoverByGenrePagingFlow(28)
        assertTrue(flow !== null)
    }

    @Test
    fun `searchPagingFlow expoe Flow do Pager para query`() = runTest {
        val flow = repository.searchPagingFlow("inception")
        assertTrue(flow !== null)
    }
}
