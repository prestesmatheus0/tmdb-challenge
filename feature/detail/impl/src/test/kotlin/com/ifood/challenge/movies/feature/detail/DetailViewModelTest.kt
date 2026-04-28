package com.ifood.challenge.movies.feature.detail

import androidx.lifecycle.SavedStateHandle
import com.ifood.challenge.movies.core.testing.MainDispatcherRule
import com.ifood.challenge.movies.domain.movies.model.Genre
import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.domain.movies.model.MovieDetail
import com.ifood.challenge.movies.domain.movies.usecase.FetchMovieDetailUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetIsFavoriteUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetMovieDetailUseCase
import com.ifood.challenge.movies.domain.movies.usecase.SetFavoriteUseCase
import com.ifood.challenge.movies.feature.detail.internal.DetailUiState
import com.ifood.challenge.movies.feature.detail.internal.DetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val detailFlow = MutableStateFlow<MovieDetail?>(null)
    private val isFavoriteFlow = MutableStateFlow(false)
    private var fetchResult: Result<Unit> = Result.success(Unit)
    private val setFavoriteCalls = mutableListOf<Pair<Movie, Boolean>>()

    private fun createViewModel(movieId: Int = MOVIE_ID) = DetailViewModel(
        savedStateHandle = SavedStateHandle().apply {
            this["movieId"] = movieId
        },
        fetchDetail = FetchMovieDetailUseCase { fetchResult.getOrThrow() },
        observeDetail = GetMovieDetailUseCase { detailFlow },
        observeIsFavorite = GetIsFavoriteUseCase { isFavoriteFlow },
        setFavorite = SetFavoriteUseCase { movie, isFavorite ->
            setFavoriteCalls.add(movie to isFavorite)
        },
    )

    @Test
    fun uiState_initially_isLoading() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        assertIs<DetailUiState.Loading>(viewModel.uiState.value)
    }

    @Test
    fun uiState_whenDetailEmitted_isSuccess() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        detailFlow.value = TEST_DETAIL

        val state = viewModel.uiState.value
        assertIs<DetailUiState.Success>(state)
        assertEquals(TEST_DETAIL.title, state.detail.title)
        assertEquals(false, state.isFavorite)
    }

    @Test
    fun uiState_whenDetailEmittedAndFavorite_successWithFavoriteTrue() = runTest {
        isFavoriteFlow.value = true
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        detailFlow.value = TEST_DETAIL

        val state = viewModel.uiState.value
        assertIs<DetailUiState.Success>(state)
        assertEquals(true, state.isFavorite)
    }

    @Test
    fun uiState_whenFetchFails_andNoCache_isError() = runTest {
        fetchResult = Result.failure(RuntimeException("network"))
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        viewModel.onViewCreated()

        assertIs<DetailUiState.Error>(viewModel.uiState.value)
    }

    @Test
    fun uiState_whenFetchFails_butCacheExists_isSuccess() = runTest {
        fetchResult = Result.failure(RuntimeException("network"))
        detailFlow.value = TEST_DETAIL
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        assertIs<DetailUiState.Success>(viewModel.uiState.value)
    }

    @Test
    fun onRetry_afterError_retriesFetch() = runTest {
        fetchResult = Result.failure(RuntimeException("network"))
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        viewModel.onViewCreated()

        assertIs<DetailUiState.Error>(viewModel.uiState.value)

        fetchResult = Result.success(Unit)
        detailFlow.value = TEST_DETAIL
        viewModel.onRetry()

        assertIs<DetailUiState.Success>(viewModel.uiState.value)
    }

    @Test
    fun onFavoriteToggle_whenNotFavorite_callsSetFavoriteTrue() = runTest {
        detailFlow.value = TEST_DETAIL
        isFavoriteFlow.value = false
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.onFavoriteToggle()

        assertEquals(1, setFavoriteCalls.size)
        assertEquals(MOVIE_ID, setFavoriteCalls[0].first.id)
        assertEquals(true, setFavoriteCalls[0].second)
    }

    @Test
    fun onFavoriteToggle_whenFavorite_callsSetFavoriteFalse() = runTest {
        detailFlow.value = TEST_DETAIL
        isFavoriteFlow.value = true
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.onFavoriteToggle()

        assertEquals(1, setFavoriteCalls.size)
        assertEquals(false, setFavoriteCalls[0].second)
    }

    @Test
    fun onFavoriteToggle_whenNotSuccess_doesNothing() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.onFavoriteToggle()

        assertEquals(0, setFavoriteCalls.size)
    }

    companion object {
        private const val MOVIE_ID = 42

        private val TEST_DETAIL = MovieDetail(
            id = MOVIE_ID,
            title = "Inception",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            overview = "A thief who steals corporate secrets",
            voteAverage = 8.8,
            releaseDate = "2010-07-16",
            runtimeMinutes = 148,
            tagline = "Your mind is the scene of the crime",
            popularity = 0.0,
            genres = listOf(Genre(28, "Action"), Genre(878, "Sci-Fi")),
        )
    }
}
