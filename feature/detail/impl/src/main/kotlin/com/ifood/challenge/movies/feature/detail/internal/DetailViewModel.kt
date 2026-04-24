package com.ifood.challenge.movies.feature.detail.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.domain.movies.model.MovieDetail
import com.ifood.challenge.movies.domain.movies.usecase.FetchMovieDetailUseCase
import com.ifood.challenge.movies.domain.movies.usecase.ObserveIsFavoriteUseCase
import com.ifood.challenge.movies.domain.movies.usecase.ObserveMovieDetailUseCase
import com.ifood.challenge.movies.domain.movies.usecase.SetFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class DetailViewModel(
    private val movieId: Int,
    private val fetchDetail: FetchMovieDetailUseCase,
    observeDetail: ObserveMovieDetailUseCase,
    observeIsFavorite: ObserveIsFavoriteUseCase,
    private val setFavorite: SetFavoriteUseCase,
) : ViewModel() {

    private val isLoading = MutableStateFlow(true)
    private val error = MutableStateFlow(false)

    val uiState = combine(
        observeDetail(movieId),
        observeIsFavorite(movieId),
        isLoading,
        error,
    ) { detail, isFavorite, loading, err ->
        DetailUiState(
            detail = detail,
            isFavorite = isFavorite,
            isLoading = loading,
            error = err,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DetailUiState(),
    )

    init {
        viewModelScope.launch {
            runCatching { fetchDetail(movieId) }
                .onSuccess { isLoading.value = false }
                .onFailure {
                    isLoading.value = false
                    error.value = true
                }
        }
    }

    fun onFavoriteToggle() {
        val detail = uiState.value.detail ?: return
        val isFavorite = uiState.value.isFavorite
        viewModelScope.launch {
            setFavorite(detail.toMovie(), isFavorite = !isFavorite)
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
