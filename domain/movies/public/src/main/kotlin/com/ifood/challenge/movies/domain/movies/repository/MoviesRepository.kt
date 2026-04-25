package com.ifood.challenge.movies.domain.movies.repository

import androidx.paging.PagingData
import com.ifood.challenge.movies.domain.movies.model.Genre
import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.domain.movies.model.MovieDetail
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    fun popularPagingFlow(): Flow<PagingData<Movie>>
    fun discoverByGenrePagingFlow(genreId: Int): Flow<PagingData<Movie>>
    fun searchPagingFlow(query: String): Flow<PagingData<Movie>>
    fun observeDetail(movieId: Int): Flow<MovieDetail?>
    fun observeIsFavorite(movieId: Int): Flow<Boolean>
    fun observeAllFavoriteIds(): Flow<Set<Int>>
    fun observeFavoriteMovies(): Flow<List<Movie>>
    suspend fun fetchGenres(): List<Genre>
    suspend fun fetchAndCacheDetail(movieId: Int)
    suspend fun setFavorite(movie: Movie, isFavorite: Boolean)
}
