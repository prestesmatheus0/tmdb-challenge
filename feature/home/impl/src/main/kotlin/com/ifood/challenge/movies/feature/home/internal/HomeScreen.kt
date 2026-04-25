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

private const val FAVORITES_KEY = -1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    uiState: HomeUiState,
    movies: LazyPagingItems<Movie>,
    onMovieClick: (movieId: Int) -> Unit,
    onGenreSelect: (genreId: Int?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onFavoriteToggle: (Movie) -> Unit,
    onFavoritesToggle: () -> Unit,
    onShuffle: () -> Unit,
    modifier: Modifier = Modifier,
    imageUrlBuilder: ImageUrlBuilder = koinInject(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Filmes") },
                actions = {
                    IconButton(onClick = onSearchToggle) {
                        Icon(
                            imageVector = if (uiState.isSearchActive) Icons.Default.SearchOff else Icons.Default.Search,
                            contentDescription = if (uiState.isSearchActive) "Fechar busca" else "Buscar",
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
                        contentDescription = "Filme aleatório dos favoritos",
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
                                placeholder = { Text("Buscar filmes…") },
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

                val modeChips = listOf(
                    MovieFilterChip<Int?>(key = null, label = "Popular"),
                    MovieFilterChip<Int?>(key = FAVORITES_KEY, label = "Favoritos"),
                )
                val genreChips = uiState.genres.map { MovieFilterChip<Int?>(key = it.id, label = it.name) }
                val allChips = if (uiState.showFavorites) {
                    modeChips
                } else {
                    modeChips + MovieFilterChip<Int?>(key = null, label = "Todos") + genreChips
                }
                val selectedChip: Int? = when {
                    uiState.showFavorites -> FAVORITES_KEY
                    else -> uiState.selectedGenreId
                }

                if (allChips.size > 1) {
                    FilterChipRow(
                        chips = allChips.distinctBy { it.key },
                        selected = selectedChip,
                        onSelect = { key ->
                            when (key) {
                                FAVORITES_KEY -> onFavoritesToggle()
                                else -> onGenreSelect(key)
                            }
                        },
                    )
                }

                when {
                    uiState.showFavorites -> FavoritesContent(
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
            title = "Nenhum favorito ainda",
            description = "Favorite filmes para vê-los aqui",
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
            title = if (isSearchActive) "Sem resultados" else "Nenhum filme encontrado",
            description = if (isSearchActive) "Tente outro termo de busca" else "Tente outro gênero",
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
