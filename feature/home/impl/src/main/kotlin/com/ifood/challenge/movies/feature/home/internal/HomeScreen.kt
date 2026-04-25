package com.ifood.challenge.movies.feature.home.internal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.ifood.challenge.movies.core.designsystem.component.EmptyState
import com.ifood.challenge.movies.core.designsystem.component.ErrorState
import com.ifood.challenge.movies.core.designsystem.component.ErrorVariant
import com.ifood.challenge.movies.core.designsystem.component.FilterChipRow
import com.ifood.challenge.movies.core.designsystem.component.MovieCard
import com.ifood.challenge.movies.core.designsystem.component.MovieCardSkeleton
import com.ifood.challenge.movies.core.designsystem.component.MovieFilterChip
import com.ifood.challenge.movies.core.designsystem.component.OfflineBanner
import com.ifood.challenge.movies.domain.movies.model.Movie
import org.koin.compose.koinInject
import com.ifood.challenge.movies.core.network.ImageUrlBuilder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    uiState: HomeUiState,
    movies: LazyPagingItems<Movie>,
    onMovieClick: (movieId: Int) -> Unit,
    onFilterSelect: (HomeFilter) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onFavoriteToggle: (Movie) -> Unit,
    onShuffle: () -> Unit,
    modifier: Modifier = Modifier,
    imageUrlBuilder: ImageUrlBuilder = koinInject(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_title)) },
                actions = {
                    IconButton(onClick = onSearchToggle) {
                        Icon(
                            imageVector = if (uiState.isSearchActive) Icons.Default.SearchOff else Icons.Default.Search,
                            contentDescription = stringResource(
                                if (uiState.isSearchActive) R.string.home_search_close else R.string.home_search_open,
                            ),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (uiState.favoriteIds.isNotEmpty()) {
                FloatingActionButton(
                    onClick = onShuffle,
                    shape = MaterialTheme.shapes.large,
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = stringResource(R.string.home_shuffle_description),
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(visible = uiState.isSearchActive) {
                    SearchBar(
                        inputField = {
                            SearchBarDefaults.InputField(
                                query = uiState.searchQuery,
                                onQueryChange = onSearchQueryChange,
                                onSearch = {},
                                expanded = false,
                                onExpandedChange = {},
                                placeholder = { Text(stringResource(R.string.home_search_placeholder)) },
                            )
                        },
                        expanded = false,
                        onExpandedChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .testTag("home_search_bar"),
                        content = {},
                    )
                }

                AnimatedVisibility(visible = uiState.isOffline) {
                    OfflineBanner()
                }

                val favCount = uiState.favoriteIds.size
                val favLabel = if (favCount > 0) {
                    stringResource(R.string.home_chip_favorites_count, favCount)
                } else {
                    stringResource(R.string.home_chip_favorites)
                }
                val modeChips = listOf(
                    MovieFilterChip<HomeFilter>(key = HomeFilter.Popular, label = stringResource(R.string.home_chip_popular)),
                    MovieFilterChip<HomeFilter>(key = HomeFilter.Favorites, label = favLabel),
                    MovieFilterChip<HomeFilter>(key = HomeFilter.NowPlaying, label = stringResource(R.string.home_chip_now_playing)),
                )
                val genreChips = uiState.genres.map {
                    MovieFilterChip<HomeFilter>(key = HomeFilter.Genre(it.id), label = it.name)
                }
                val allChips = modeChips + genreChips

                if (allChips.size > 1) {
                    FilterChipRow(
                        chips = allChips,
                        selected = uiState.filter,
                        onSelect = onFilterSelect,
                    )
                }

                when (uiState.filter) {
                    HomeFilter.Favorites -> FavoritesContent(
                        movies = uiState.favoriteMovies,
                        favoriteIds = uiState.favoriteIds,
                        imageUrlBuilder = imageUrlBuilder,
                        onMovieClick = onMovieClick,
                        onFavoriteToggle = onFavoriteToggle,
                    )
                    else -> PagingContent(
                        movies = movies,
                        favoriteIds = uiState.favoriteIds,
                        isOffline = uiState.isOffline,
                        isSearchActive = uiState.isSearchActive,
                        imageUrlBuilder = imageUrlBuilder,
                        onMovieClick = onMovieClick,
                        onFavoriteToggle = onFavoriteToggle,
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesContent(
    movies: List<Movie>,
    favoriteIds: Set<Int>,
    imageUrlBuilder: ImageUrlBuilder,
    onMovieClick: (Int) -> Unit,
    onFavoriteToggle: (Movie) -> Unit,
) {
    if (movies.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Movie,
            title = stringResource(R.string.home_favorites_empty_title),
            description = stringResource(R.string.home_favorites_empty_description),
            modifier = Modifier.fillMaxSize(),
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(count = movies.size, key = { movies[it].id }) { index ->
                val movie = movies[index]
                MovieCard(
                    title = movie.title,
                    posterUrl = imageUrlBuilder.poster(movie.posterPath),
                    rating = movie.voteAverage,
                    isFavorite = movie.id in favoriteIds,
                    onClick = { onMovieClick(movie.id) },
                    onFavoriteToggle = { onFavoriteToggle(movie) },
                )
            }
        }
    }
}

@Composable
private fun PagingContent(
    movies: LazyPagingItems<Movie>,
    favoriteIds: Set<Int>,
    isOffline: Boolean,
    isSearchActive: Boolean,
    imageUrlBuilder: ImageUrlBuilder,
    onMovieClick: (Int) -> Unit,
    onFavoriteToggle: (Movie) -> Unit,
) {
    val refreshState = movies.loadState.refresh
    when {
        refreshState is LoadState.Loading && movies.itemCount == 0 -> SkeletonGrid()
        refreshState is LoadState.Error && movies.itemCount == 0 -> ErrorState(
            variant = if (isOffline) ErrorVariant.Network else ErrorVariant.Generic,
            onRetry = { movies.retry() },
            modifier = Modifier.fillMaxSize(),
        )
        movies.itemCount == 0 && refreshState is LoadState.NotLoading -> EmptyState(
            icon = if (isSearchActive) Icons.Default.SearchOff else Icons.Default.Movie,
            title = stringResource(if (isSearchActive) R.string.home_search_empty_title else R.string.home_movies_empty_title),
            description = stringResource(if (isSearchActive) R.string.home_search_empty_description else R.string.home_movies_empty_description),
            modifier = Modifier.fillMaxSize(),
        )
        else -> MovieGrid(
            movies = movies,
            favoriteIds = favoriteIds,
            imageUrlBuilder = imageUrlBuilder,
            onMovieClick = onMovieClick,
            onFavoriteToggle = onFavoriteToggle,
        )
    }
}

@Composable
private fun MovieGrid(
    movies: LazyPagingItems<Movie>,
    favoriteIds: Set<Int>,
    imageUrlBuilder: ImageUrlBuilder,
    onMovieClick: (movieId: Int) -> Unit,
    onFavoriteToggle: (Movie) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(
            count = movies.itemCount,
            key = movies.itemKey { it.id },
        ) { index ->
            val movie = movies[index]
            if (movie != null) {
                MovieCard(
                    title = movie.title,
                    posterUrl = imageUrlBuilder.poster(movie.posterPath),
                    rating = movie.voteAverage,
                    isFavorite = movie.id in favoriteIds,
                    onClick = { onMovieClick(movie.id) },
                    onFavoriteToggle = { onFavoriteToggle(movie) },
                )
            } else {
                MovieCardSkeleton()
            }
        }

        if (movies.loadState.append is LoadState.Loading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun SkeletonGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(6) { MovieCardSkeleton() }
    }
}
