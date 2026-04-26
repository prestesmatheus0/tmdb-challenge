package com.ifood.challenge.movies.feature.home

import androidx.paging.PagingData
import com.ifood.challenge.movies.core.common.network.ConnectivityObserver
import com.ifood.challenge.movies.core.common.network.NetworkStatus
import com.ifood.challenge.movies.core.testing.MainDispatcherRule
import com.ifood.challenge.movies.domain.movies.model.Genre
import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.domain.movies.usecase.GetFavoriteMoviesUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetGenresUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetMoviesByGenreUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetMoviesByQueryUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetNowPlayingMoviesUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetPopularMoviesUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetFavoriteIdsUseCase
import com.ifood.challenge.movies.domain.movies.usecase.SetFavoriteUseCase
import com.ifood.challenge.movies.feature.home.internal.HomeFilter
import com.ifood.challenge.movies.feature.home.internal.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val favoriteIdsFlow = MutableStateFlow<Set<Int>>(emptySet())
    private val favoriteMoviesFlow = MutableStateFlow<List<Movie>>(emptyList())
    private val connectivityFlow = MutableStateFlow(NetworkStatus.Online)
    private var genresResult: Result<List<Genre>> = Result.success(TEST_GENRES)
    private val setFavoriteCalls = mutableListOf<Pair<Movie, Boolean>>()

    private fun createViewModel() = HomeViewModel(
        getPopularMovies = GetPopularMoviesUseCase { emptyFlow() },
        getNowPlayingMovies = GetNowPlayingMoviesUseCase { emptyFlow() },
        getMoviesByGenre = GetMoviesByGenreUseCase { emptyFlow() },
        getMoviesByQuery = GetMoviesByQueryUseCase { emptyFlow() },
        getGenres = GetGenresUseCase { genresResult.getOrThrow() },
        setFavorite = SetFavoriteUseCase { movie, isFavorite ->
            setFavoriteCalls.add(movie to isFavorite)
        },
        getFavoriteMovies = GetFavoriteMoviesUseCase { favoriteMoviesFlow },
        observeFavoriteIds = GetFavoriteIdsUseCase { favoriteIdsFlow },
        connectivityObserver = object : ConnectivityObserver {
            override fun observe() = connectivityFlow
        },
    )

    @Test
    fun uiState_initially_hasDefaultValues() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        val state = viewModel.uiState.value
        assertIs<HomeFilter.Popular>(state.filter)
        assertEquals("", state.searchQuery)
        assertFalse(state.isSearchActive)
        assertFalse(state.isOffline)
    }

    @Test
    fun uiState_afterInit_loadsGenres() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        assertEquals(TEST_GENRES, viewModel.uiState.value.genres)
        assertFalse(viewModel.uiState.value.genresError)
    }

    @Test
    fun uiState_whenGenresFail_setsGenresError() = runTest {
        genresResult = Result.failure(RuntimeException("network"))
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        assertTrue(viewModel.uiState.value.genresError)
        assertTrue(viewModel.uiState.value.genres.isEmpty())
    }

    @Test
    fun uiState_whenOffline_isOfflineTrue() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        connectivityFlow.value = NetworkStatus.Offline
        assertTrue(viewModel.uiState.value.isOffline)

        connectivityFlow.value = NetworkStatus.Online
        assertFalse(viewModel.uiState.value.isOffline)
    }

    @Test
    fun uiState_whenFavoriteIdsChange_updatesState() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        favoriteIdsFlow.value = setOf(1, 2, 3)
        assertEquals(setOf(1, 2, 3), viewModel.uiState.value.favoriteIds)
    }

    @Test
    fun uiState_whenFavoriteMoviesChange_updatesState() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        favoriteMoviesFlow.value = listOf(TEST_MOVIE)
        assertEquals(1, viewModel.uiState.value.favoriteMovies.size)
        assertEquals(TEST_MOVIE.id, viewModel.uiState.value.favoriteMovies[0].id)
    }

    @Test
    fun onFilterSelect_updatesFilter() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.onFilterSelect(HomeFilter.Favorites)
        assertIs<HomeFilter.Favorites>(viewModel.uiState.value.filter)

        viewModel.onFilterSelect(HomeFilter.NowPlaying)
        assertIs<HomeFilter.NowPlaying>(viewModel.uiState.value.filter)

        viewModel.onFilterSelect(HomeFilter.Genre(28))
        val genre = viewModel.uiState.value.filter
        assertIs<HomeFilter.Genre>(genre)
        assertEquals(28, genre.genreId)
    }

    @Test
    fun onFilterSelect_clearsSearchState() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.onSearchToggle()
        viewModel.onSearchQueryChange("test")
        assertTrue(viewModel.uiState.value.isSearchActive)

        viewModel.onFilterSelect(HomeFilter.Favorites)
        assertEquals("", viewModel.uiState.value.searchQuery)
        assertFalse(viewModel.uiState.value.isSearchActive)
    }

    @Test
    fun onSearchToggle_togglesSearchActive() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.onSearchToggle()
        assertTrue(viewModel.uiState.value.isSearchActive)

        viewModel.onSearchToggle()
        assertFalse(viewModel.uiState.value.isSearchActive)
        assertEquals("", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun onSearchToggle_whenInFavorites_switchesToPopular() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.onFilterSelect(HomeFilter.Favorites)
        viewModel.onSearchToggle()

        assertIs<HomeFilter.Popular>(viewModel.uiState.value.filter)
        assertTrue(viewModel.uiState.value.isSearchActive)
    }

    @Test
    fun onSearchQueryChange_updatesQuery() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.onSearchQueryChange("inception")
        assertEquals("inception", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun onSearchQueryChange_whenInFavorites_switchesToPopular() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.onFilterSelect(HomeFilter.Favorites)
        viewModel.onSearchQueryChange("test")

        assertIs<HomeFilter.Popular>(viewModel.uiState.value.filter)
    }

    @Test
    fun onFavoriteToggle_whenNotFavorite_callsSetFavoriteTrue() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        favoriteIdsFlow.value = emptySet()
        viewModel.onFavoriteToggle(TEST_MOVIE)

        assertEquals(1, setFavoriteCalls.size)
        assertEquals(true, setFavoriteCalls[0].second)
    }

    @Test
    fun onFavoriteToggle_whenAlreadyFavorite_callsSetFavoriteFalse() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        favoriteIdsFlow.value = setOf(TEST_MOVIE.id)
        viewModel.onFavoriteToggle(TEST_MOVIE)

        assertEquals(1, setFavoriteCalls.size)
        assertEquals(false, setFavoriteCalls[0].second)
    }

    @Test
    fun onShuffle_withFavorites_emitsRandomMovieId() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        val movie2 = TEST_MOVIE.copy(id = 99, title = "Movie 2")
        favoriteMoviesFlow.value = listOf(TEST_MOVIE, movie2)

        var emittedId: Int? = null
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.shuffleEvent.collect { emittedId = it }
        }

        viewModel.onShuffle()

        assertTrue(emittedId == TEST_MOVIE.id || emittedId == movie2.id)
    }

    @Test
    fun onShuffle_withoutFavorites_doesNotEmit() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        var emittedId: Int? = null
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.shuffleEvent.collect { emittedId = it }
        }

        viewModel.onShuffle()

        assertEquals(null, emittedId)
    }

    companion object {
        private val TEST_GENRES = listOf(
            Genre(28, "Ação"),
            Genre(12, "Aventura"),
            Genre(16, "Animação"),
        )

        private val TEST_MOVIE = Movie(
            id = 1,
            title = "Inception",
            posterPath = "/poster.jpg",
            backdropPath = null,
            overview = "A thief",
            voteAverage = 8.8,
            releaseDate = "2010-07-16",
            popularity = 100.0,
        )
    }
}
