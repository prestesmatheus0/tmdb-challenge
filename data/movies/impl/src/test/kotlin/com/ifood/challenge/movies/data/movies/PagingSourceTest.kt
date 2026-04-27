package com.ifood.challenge.movies.data.movies

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.ifood.challenge.movies.data.movies.internal.api.TmdbApiService
import com.ifood.challenge.movies.data.movies.internal.api.dto.MovieDto
import com.ifood.challenge.movies.data.movies.internal.api.dto.MovieListResponseDto
import com.ifood.challenge.movies.data.movies.internal.paging.DiscoverPagingSource
import com.ifood.challenge.movies.data.movies.internal.paging.NowPlayingPagingSource
import com.ifood.challenge.movies.data.movies.internal.paging.SearchPagingSource
import com.ifood.challenge.movies.domain.movies.model.Movie
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class PagingSourceTest {

    private val apiService = mockk<TmdbApiService>()

    // ── NowPlayingPagingSource ──

    @Test
    fun nowPlaying_firstPage_returnsPrevKeyNull() = runTest {
        coEvery { apiService.nowPlaying(1) } returns makeResponse(page = 1, totalPages = 3)

        val source = NowPlayingPagingSource(apiService)
        val result = source.load(LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false))

        assertIs<LoadResult.Page<Int, *>>(result)
        assertNull(result.prevKey)
        assertEquals(2, result.nextKey)
        assertEquals(2, result.data.size)
    }

    @Test
    fun nowPlaying_middlePage_hasBothKeys() = runTest {
        coEvery { apiService.nowPlaying(2) } returns makeResponse(page = 2, totalPages = 3)

        val source = NowPlayingPagingSource(apiService)
        val result = source.load(LoadParams.Append(key = 2, loadSize = 20, placeholdersEnabled = false))

        assertIs<LoadResult.Page<Int, *>>(result)
        assertEquals(1, result.prevKey)
        assertEquals(3, result.nextKey)
    }

    @Test
    fun nowPlaying_lastPage_returnsNextKeyNull() = runTest {
        coEvery { apiService.nowPlaying(3) } returns makeResponse(page = 3, totalPages = 3)

        val source = NowPlayingPagingSource(apiService)
        val result = source.load(LoadParams.Append(key = 3, loadSize = 20, placeholdersEnabled = false))

        assertIs<LoadResult.Page<Int, *>>(result)
        assertNull(result.nextKey)
    }

    @Test
    fun nowPlaying_onApiError_returnsLoadResultError() = runTest {
        coEvery { apiService.nowPlaying(any()) } throws RuntimeException("network")

        val source = NowPlayingPagingSource(apiService)
        val result = source.load(LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false))

        assertIs<LoadResult.Error<Int, *>>(result)
    }

    // ── DiscoverPagingSource ──

    @Test
    fun discover_firstPage_returnsPrevKeyNull() = runTest {
        coEvery { apiService.discover(page = 1, genreId = 28) } returns makeResponse(page = 1, totalPages = 5)

        val source = DiscoverPagingSource(apiService, genreId = 28)
        val result = source.load(LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false))

        assertIs<LoadResult.Page<Int, *>>(result)
        assertNull(result.prevKey)
        assertEquals(2, result.nextKey)
        assertEquals(2, result.data.size)
    }

    @Test
    fun discover_lastPage_returnsNextKeyNull() = runTest {
        coEvery { apiService.discover(page = 5, genreId = 28) } returns makeResponse(page = 5, totalPages = 5)

        val source = DiscoverPagingSource(apiService, genreId = 28)
        val result = source.load(LoadParams.Append(key = 5, loadSize = 20, placeholdersEnabled = false))

        assertIs<LoadResult.Page<Int, *>>(result)
        assertNull(result.nextKey)
    }

    @Test
    fun discover_onApiError_returnsLoadResultError() = runTest {
        coEvery { apiService.discover(page = any(), genreId = any()) } throws RuntimeException("fail")

        val source = DiscoverPagingSource(apiService, genreId = 28)
        val result = source.load(LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false))

        assertIs<LoadResult.Error<Int, *>>(result)
    }

    @Test
    fun discover_mapsMovieDtoToDomain() = runTest {
        coEvery { apiService.discover(page = 1, genreId = 16) } returns MovieListResponseDto(
            page = 1,
            totalPages = 1,
            results = listOf(
                MovieDto(id = 42, title = "Toy Story", voteAverage = 8.3, posterPath = "/ts.jpg"),
            ),
        )

        val source = DiscoverPagingSource(apiService, genreId = 16)
        val result = source.load(LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false))

        assertIs<LoadResult.Page<Int, Movie>>(result)
        val movie = result.data[0]
        assertEquals(42, movie.id)
        assertEquals("Toy Story", movie.title)
        assertEquals(8.3, movie.voteAverage)
    }

    // ── SearchPagingSource ──

    @Test
    fun search_firstPage_returnsPrevKeyNull() = runTest {
        coEvery { apiService.searchMovies(query = "inception", page = 1) } returns makeResponse(page = 1, totalPages = 4)

        val source = SearchPagingSource(apiService, query = "inception")
        val result = source.load(LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false))

        assertIs<LoadResult.Page<Int, *>>(result)
        assertNull(result.prevKey)
        assertEquals(2, result.nextKey)
    }

    @Test
    fun search_lastPage_returnsNextKeyNull() = runTest {
        coEvery { apiService.searchMovies(query = "rare", page = 1) } returns makeResponse(page = 1, totalPages = 1)

        val source = SearchPagingSource(apiService, query = "rare")
        val result = source.load(LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false))

        assertIs<LoadResult.Page<Int, *>>(result)
        assertNull(result.nextKey)
    }

    @Test
    fun search_onApiError_returnsLoadResultError() = runTest {
        coEvery { apiService.searchMovies(query = any(), page = any()) } throws RuntimeException("network")

        val source = SearchPagingSource(apiService, query = "x")
        val result = source.load(LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false))

        assertIs<LoadResult.Error<Int, *>>(result)
    }

    // ── getRefreshKey ──

    @Test
    fun nowPlaying_getRefreshKey_returnsNullWhenNoAnchor() {
        val source = NowPlayingPagingSource(apiService)
        val state = PagingState<Int, Movie>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0,
        )
        assertNull(source.getRefreshKey(state))
    }

    @Test
    fun discover_getRefreshKey_returnsNullWhenNoAnchor() {
        val source = DiscoverPagingSource(apiService, genreId = 28)
        val state = PagingState<Int, Movie>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0,
        )
        assertNull(source.getRefreshKey(state))
    }

    @Test
    fun search_getRefreshKey_returnsNullWhenNoAnchor() {
        val source = SearchPagingSource(apiService, query = "x")
        val state = PagingState<Int, Movie>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0,
        )
        assertNull(source.getRefreshKey(state))
    }

    @Test
    fun nowPlaying_getRefreshKey_derivesFromAnchorPage() = runTest {
        coEvery { apiService.nowPlaying(2) } returns makeResponse(page = 2, totalPages = 5)

        val source = NowPlayingPagingSource(apiService)
        val page = source.load(LoadParams.Append(key = 2, loadSize = 20, placeholdersEnabled = false))
            as LoadResult.Page<Int, Movie>
        val state = PagingState(
            pages = listOf(page),
            anchorPosition = 0,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0,
        )
        // closest page has prevKey=1, so refresh key = prevKey + 1 = 2
        assertEquals(2, source.getRefreshKey(state))
    }

    private fun makeResponse(page: Int, totalPages: Int) = MovieListResponseDto(
        page = page,
        totalPages = totalPages,
        results = listOf(
            MovieDto(id = 1, title = "Movie A"),
            MovieDto(id = 2, title = "Movie B"),
        ),
    )
}
