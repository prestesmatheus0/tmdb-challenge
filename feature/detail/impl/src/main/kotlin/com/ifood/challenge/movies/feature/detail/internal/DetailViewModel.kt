package com.ifood.challenge.movies.feature.detail.internal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.domain.movies.model.MovieDetail
import com.ifood.challenge.movies.domain.movies.usecase.FetchMovieDetailUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetIsFavoriteUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetMovieDetailUseCase
import com.ifood.challenge.movies.domain.movies.usecase.SetFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val fetchDetail: FetchMovieDetailUseCase,
    observeDetail: GetMovieDetailUseCase,
    observeIsFavorite: GetIsFavoriteUseCase,
    private val setFavorite: SetFavoriteUseCase,
) : ViewModel() {

    private val movieId: Int = requireNotNull(savedStateHandle[KEY_MOVIE_ID]) {
        "DetailViewModel requires '$KEY_MOVIE_ID' in SavedStateHandle"
    }

    private companion object {
        const val KEY_MOVIE_ID = "movieId"
    }

    private val isFetching = MutableStateFlow(true)
    private val fetchFailed = MutableStateFlow(false)

    val uiState = combine(
        observeDetail(movieId),
        observeIsFavorite(movieId),
        isFetching,
        fetchFailed,
    ) { detail, isFavorite, fetching, failed ->
        when {
            detail != null -> DetailUiState.Success(detail, isFavorite)
            failed && !fetching -> DetailUiState.Error
            else -> DetailUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DetailUiState.Loading,
    )

    init {
        loadDetail()
    }

    fun onRetry() {
        if (isFetching.value) return
        fetchFailed.value = false
        isFetching.value = true
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            runCatching { fetchDetail(movieId) }
                .onSuccess { isFetching.value = false }
                .onFailure {
                    isFetching.value = false
                    fetchFailed.value = true
                }
        }
    }

    fun onFavoriteToggle() {
        val current = uiState.value as? DetailUiState.Success ?: return
        viewModelScope.launch {
            setFavorite(current.detail.toMovie(), isFavorite = !current.isFavorite)
        }
    }
}

private fun MovieDetail.toMovie() = Movie(
    id = id,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    overview = overview,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    popularity = 0.0,
)
