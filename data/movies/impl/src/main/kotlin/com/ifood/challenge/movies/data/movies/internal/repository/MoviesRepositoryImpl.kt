package com.ifood.challenge.movies.data.movies.internal.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.ifood.challenge.movies.core.database.dao.FavoriteDao
import com.ifood.challenge.movies.core.database.dao.MovieDetailDao
import com.ifood.challenge.movies.core.database.dao.MovieDao
import com.ifood.challenge.movies.core.database.internal.MoviesDatabase
import com.ifood.challenge.movies.data.movies.internal.api.TmdbApiService
import com.ifood.challenge.movies.data.movies.internal.mapper.toDomain
import com.ifood.challenge.movies.data.movies.internal.mapper.toEntity
import com.ifood.challenge.movies.data.movies.internal.mapper.toFavoriteEntity
import com.ifood.challenge.movies.data.movies.internal.paging.DiscoverPagingSource
import com.ifood.challenge.movies.data.movies.internal.paging.MoviesRemoteMediator
import com.ifood.challenge.movies.domain.movies.model.Genre
import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.domain.movies.model.MovieDetail
import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PAGE_SIZE = 20

@OptIn(ExperimentalPagingApi::class)
internal class MoviesRepositoryImpl(
    private val apiService: TmdbApiService,
    private val db: MoviesDatabase,
    private val movieDao: MovieDao,
    private val movieDetailDao: MovieDetailDao,
    private val favoriteDao: FavoriteDao,
) : MoviesRepository {

    override fun popularPagingFlow(): Flow<PagingData<Movie>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = MoviesRemoteMediator(apiService, db),
            pagingSourceFactory = { movieDao.pagingSource() },
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }

    override fun discoverByGenrePagingFlow(genreId: Int): Flow<PagingData<Movie>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { DiscoverPagingSource(apiService, genreId) },
        ).flow

    override fun observeDetail(movieId: Int): Flow<MovieDetail?> =
        movieDetailDao.observe(movieId).map { it?.toDomain() }

    override fun observeIsFavorite(movieId: Int): Flow<Boolean> =
        favoriteDao.observeIsFavorite(movieId)

    override suspend fun fetchGenres(): List<Genre> =
        apiService.genres().genres.map { it.toDomain() }

    override suspend fun fetchAndCacheDetail(movieId: Int) {
        val detail = apiService.movieDetail(movieId)
        movieDetailDao.upsert(detail.toEntity())
    }

    override suspend fun setFavorite(movie: Movie, isFavorite: Boolean) {
        if (isFavorite) {
            favoriteDao.upsert(movie.toFavoriteEntity())
        } else {
            favoriteDao.delete(movie.id)
        }
    }
}
