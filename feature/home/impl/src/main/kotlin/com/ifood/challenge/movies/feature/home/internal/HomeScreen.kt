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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.ifood.challenge.movies.core.network.ImageUrlBuilder
import com.ifood.challenge.movies.domain.movies.model.Genre
import com.ifood.challenge.movies.domain.movies.model.Movie
import org.koin.compose.koinInject

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
    modifier: Modifier = Modifier,
    imageUrlBuilder: ImageUrlBuilder = koinInject(),
) {
    Column(modifier = modifier.fillMaxSize()) {
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
        )

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

        if (uiState.genres.isNotEmpty()) {
            val allChip = MovieFilterChip<Int?>(key = null, label = "Todos")
            val genreChips = uiState.genres.map { MovieFilterChip<Int?>(key = it.id, label = it.name) }
            FilterChipRow(
                chips = listOf(allChip) + genreChips,
                selected = uiState.selectedGenreId,
                onSelect = onGenreSelect,
            )
        }

        val refreshState = movies.loadState.refresh
        when {
            refreshState is LoadState.Loading && movies.itemCount == 0 -> SkeletonGrid()
            refreshState is LoadState.Error && movies.itemCount == 0 -> ErrorState(
                variant = if (uiState.isOffline) ErrorVariant.Network else ErrorVariant.Generic,
                onRetry = { movies.retry() },
                modifier = Modifier.fillMaxSize(),
            )
            movies.itemCount == 0 && refreshState is LoadState.NotLoading -> EmptyState(
                icon = Icons.Default.Movie,
                title = "Nenhum filme encontrado",
                description = "Tente outra busca ou gênero",
                modifier = Modifier.fillMaxSize(),
            )
            else -> MovieGrid(
                movies = movies,
                favoriteIds = uiState.favoriteIds,
                imageUrlBuilder = imageUrlBuilder,
                onMovieClick = onMovieClick,
                onFavoriteToggle = onFavoriteToggle,
            )
        }
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
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
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
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(6) { MovieCardSkeleton() }
    }
}
