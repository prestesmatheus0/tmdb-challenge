package com.ifood.challenge.movies.feature.home.internal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ifood.challenge.movies.core.common.network.ConnectivityObserver
import com.ifood.challenge.movies.core.common.network.NetworkStatus
import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.domain.movies.usecase.GetFavoriteIdsUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetFavoriteMoviesUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetGenresUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetMoviesByGenreUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetMoviesByQueryUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetNowPlayingMoviesUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetPopularMoviesUseCase
import com.ifood.challenge.movies.domain.movies.usecase.SetFavoriteUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SEARCH_MIN_LENGTH = 2

@OptIn(ExperimentalCoroutinesApi::class)
internal class HomeViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getPopularMovies: GetPopularMoviesUseCase,
    private val getNowPlayingMovies: GetNowPlayingMoviesUseCase,
    private val getMoviesByGenre: GetMoviesByGenreUseCase,
    private val getMoviesByQuery: GetMoviesByQueryUseCase,
    private val getGenres: GetGenresUseCase,
    private val setFavorite: SetFavoriteUseCase,
    private val getFavoriteMovies: GetFavoriteMoviesUseCase,
    observeFavoriteIds: GetFavoriteIdsUseCase,
    connectivityObserver: ConnectivityObserver,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            searchQuery = savedStateHandle[KEY_SEARCH_QUERY] ?: "",
            isSearchActive = savedStateHandle[KEY_SEARCH_ACTIVE] ?: false,
            filter = decodeFilter(savedStateHandle),
        ),
    )

    // initialValue mirrors the SavedStateHandle-restored state so that recomposition
    // immediately after process death sees the persisted filter/searchQuery, not the default.
    val uiState = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _uiState.value,
    )

    private val _shuffleEvent = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val shuffleEvent = _shuffleEvent.asSharedFlow()

    val moviesPagingFlow: Flow<PagingData<Movie>> =
        combine(
            _uiState.map { it.searchQuery }.distinctUntilChanged(),
            _uiState.map { it.filter }.distinctUntilChanged(),
        ) { query, filter -> query to filter }
            .flatMapLatest { (query, filter) ->
                when {
                    // Favorites are served from a separate flow (`favoriteMovies`),
                    // so the paging stream stops emitting until the user picks another filter.
                    filter is HomeFilter.Favorites -> emptyFlow()
                    query.length >= SEARCH_MIN_LENGTH -> getMoviesByQuery(query)
                    filter is HomeFilter.Genre -> getMoviesByGenre(filter.genreId)
                    filter is HomeFilter.NowPlaying -> getNowPlayingMovies()
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
            getFavoriteMovies().collect { movies ->
                _uiState.update { it.copy(favoriteMovies = movies) }
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
        _uiState.update {
            it.copy(
                searchQuery = query,
                filter = if (it.filter is HomeFilter.Favorites) HomeFilter.Popular else it.filter,
            )
        }
        savedStateHandle[KEY_SEARCH_QUERY] = query
        if (_uiState.value.filter is HomeFilter.Popular) {
            persistFilter(HomeFilter.Popular)
        }
    }

    fun onSearchToggle() {
        _uiState.update { state ->
            if (state.isSearchActive) {
                state.copy(isSearchActive = false, searchQuery = "")
            } else {
                state.copy(
                    isSearchActive = true,
                    filter = if (state.filter is HomeFilter.Favorites) HomeFilter.Popular else state.filter,
                )
            }
        }
        savedStateHandle[KEY_SEARCH_ACTIVE] = _uiState.value.isSearchActive
        savedStateHandle[KEY_SEARCH_QUERY] = _uiState.value.searchQuery
        persistFilter(_uiState.value.filter)
    }

    fun onFilterSelect(filter: HomeFilter) {
        _uiState.update {
            it.copy(filter = filter, searchQuery = "", isSearchActive = false)
        }
        persistFilter(filter)
        savedStateHandle[KEY_SEARCH_QUERY] = ""
        savedStateHandle[KEY_SEARCH_ACTIVE] = false
    }

    fun onFavoriteToggle(movie: Movie) {
        val isCurrentlyFavorite = movie.id in _uiState.value.favoriteIds
        viewModelScope.launch {
            setFavorite(movie, isFavorite = !isCurrentlyFavorite)
        }
    }

    fun onShuffle() {
        val favorites = _uiState.value.favoriteMovies
        if (favorites.isNotEmpty()) {
            _shuffleEvent.tryEmit(favorites.random().id)
        }
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private fun loadGenres() {
        viewModelScope.launch {
            try {
                val genres = getGenres()
                _uiState.update { it.copy(genres = genres, genresError = false) }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(genresError = true) }
            }
        }
    }

    private fun persistFilter(filter: HomeFilter) {
        savedStateHandle[KEY_FILTER_TYPE] = when (filter) {
            HomeFilter.Popular -> FILTER_POPULAR
            HomeFilter.NowPlaying -> FILTER_NOW_PLAYING
            HomeFilter.Favorites -> FILTER_FAVORITES
            is HomeFilter.Genre -> FILTER_GENRE
        }
        savedStateHandle[KEY_FILTER_GENRE_ID] = (filter as? HomeFilter.Genre)?.genreId ?: -1
    }

    private companion object {
        const val KEY_SEARCH_QUERY = "home_search_query"
        const val KEY_SEARCH_ACTIVE = "home_search_active"
        const val KEY_FILTER_TYPE = "home_filter_type"
        const val KEY_FILTER_GENRE_ID = "home_filter_genre_id"

        const val FILTER_POPULAR = "popular"
        const val FILTER_NOW_PLAYING = "now_playing"
        const val FILTER_FAVORITES = "favorites"
        const val FILTER_GENRE = "genre"

        fun decodeFilter(handle: SavedStateHandle): HomeFilter = when (handle.get<String>(KEY_FILTER_TYPE)) {
            FILTER_NOW_PLAYING -> HomeFilter.NowPlaying
            FILTER_FAVORITES -> HomeFilter.Favorites
            FILTER_GENRE -> HomeFilter.Genre(handle[KEY_FILTER_GENRE_ID] ?: 0)
            else -> HomeFilter.Popular
        }
    }
}
