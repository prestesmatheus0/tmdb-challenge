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
import com.ifood.challenge.movies.data.movies.internal.paging.NowPlayingPagingSource
import com.ifood.challenge.movies.data.movies.internal.paging.SearchPagingSource
import com.ifood.challenge.movies.core.common.coroutines.DispatcherProvider
import com.ifood.challenge.movies.domain.movies.model.Genre
import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.domain.movies.model.MovieDetail
import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val PAGE_SIZE = 20

@OptIn(ExperimentalPagingApi::class)
internal class MoviesRepositoryImpl(
    private val apiService: TmdbApiService,
    private val db: MoviesDatabase,
    private val movieDao: MovieDao,
    private val movieDetailDao: MovieDetailDao,
    private val favoriteDao: FavoriteDao,
    private val dispatchers: DispatcherProvider,
) : MoviesRepository {

    override fun popularPagingFlow(): Flow<PagingData<Movie>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = MoviesRemoteMediator(apiService, db),
            pagingSourceFactory = { movieDao.pagingSource() },
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }

    override fun nowPlayingPagingFlow(): Flow<PagingData<Movie>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { NowPlayingPagingSource(apiService) },
        ).flow

    override fun discoverByGenrePagingFlow(genreId: Int): Flow<PagingData<Movie>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { DiscoverPagingSource(apiService, genreId) },
        ).flow

    override fun searchPagingFlow(query: String): Flow<PagingData<Movie>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { SearchPagingSource(apiService, query) },
        ).flow

    override fun observeDetail(movieId: Int): Flow<MovieDetail?> =
        movieDetailDao.observe(movieId).map { it?.toDomain() }

    override fun observeIsFavorite(movieId: Int): Flow<Boolean> =
        favoriteDao.observeIsFavorite(movieId)

    override fun observeAllFavoriteIds(): Flow<Set<Int>> =
        favoriteDao.observeAll().map { list -> list.map { it.movieId }.toSet() }

    override fun observeFavoriteMovies(): Flow<List<Movie>> =
        favoriteDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun fetchGenres(): List<Genre> = withContext(dispatchers.io) {
        apiService.genres().genres.map { it.toDomain() }
    }

    override suspend fun fetchAndCacheDetail(movieId: Int) = withContext(dispatchers.io) {
        val detail = apiService.movieDetail(movieId)
        movieDetailDao.upsert(detail.toEntity())
    }

    override suspend fun setFavorite(movie: Movie, isFavorite: Boolean) = withContext(dispatchers.io) {
        if (isFavorite) {
            favoriteDao.upsert(movie.toFavoriteEntity())
        } else {
            favoriteDao.delete(movie.id)
        }
    }
}
