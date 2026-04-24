package com.ifood.challenge.movies.feature.home.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ifood.challenge.movies.core.common.network.ConnectivityObserver
import com.ifood.challenge.movies.core.common.network.NetworkStatus
import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.domain.movies.usecase.GetGenresUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetMoviesByGenreUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetMoviesByQueryUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetPopularMoviesUseCase
import com.ifood.challenge.movies.domain.movies.usecase.ObserveFavoriteIdsUseCase
import com.ifood.challenge.movies.domain.movies.usecase.SetFavoriteUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SEARCH_MIN_LENGTH = 2

@OptIn(ExperimentalCoroutinesApi::class)
internal class HomeViewModel(
    private val getPopularMovies: GetPopularMoviesUseCase,
    private val getMoviesByGenre: GetMoviesByGenreUseCase,
    private val getMoviesByQuery: GetMoviesByQueryUseCase,
    private val getGenres: GetGenresUseCase,
    private val setFavorite: SetFavoriteUseCase,
    observeFavoriteIds: ObserveFavoriteIdsUseCase,
    connectivityObserver: ConnectivityObserver,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )

    val moviesPagingFlow: Flow<PagingData<Movie>> =
        combine(
            _uiState.map { it.searchQuery }.distinctUntilChanged(),
            _uiState.map { it.selectedGenreId }.distinctUntilChanged(),
        ) { query, genreId -> query to genreId }
            .flatMapLatest { (query, genreId) ->
                when {
                    query.length >= SEARCH_MIN_LENGTH -> getMoviesByQuery(query)
                    genreId != null -> getMoviesByGenre(genreId)
                    else -> getPopularMovies()
                }
            }
            .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            observeFavoriteIds().collect { ids ->
                _uiState.update { it.copy(favoriteIds = ids) }
            }
        }
        viewModelScope.launch {
            connectivityObserver.observe().collect { status ->
                _uiState.update { it.copy(isOffline = status == NetworkStatus.Offline) }
            }
        }
        loadGenres()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query, selectedGenreId = if (query.isNotEmpty()) null else it.selectedGenreId) }
    }

    fun onSearchToggle() {
        _uiState.update { state ->
            if (state.isSearchActive) {
                state.copy(isSearchActive = false, searchQuery = "")
            } else {
                state.copy(isSearchActive = true)
            }
        }
    }

    fun onGenreSelect(genreId: Int?) {
        _uiState.update { it.copy(selectedGenreId = genreId, searchQuery = "", isSearchActive = false) }
    }

    fun onFavoriteToggle(movie: Movie) {
        val isCurrentlyFavorite = movie.id in _uiState.value.favoriteIds
        viewModelScope.launch {
            setFavorite(movie, isFavorite = !isCurrentlyFavorite)
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            runCatching { getGenres() }
                .onSuccess { genres -> _uiState.update { it.copy(genres = genres, genresError = false) } }
                .onFailure { _uiState.update { it.copy(genresError = true) } }
        }
    }
}
