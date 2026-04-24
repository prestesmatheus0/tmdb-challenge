package com.ifood.challenge.movies.data.movies.internal.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ifood.challenge.movies.core.database.entity.MovieEntity
import com.ifood.challenge.movies.core.database.entity.RemoteKeyEntity
import com.ifood.challenge.movies.core.database.internal.MoviesDatabase
import com.ifood.challenge.movies.data.movies.internal.api.TmdbApiService
import com.ifood.challenge.movies.data.movies.internal.mapper.toEntity
import kotlinx.coroutines.CancellationException

private const val STARTING_PAGE = 1
private const val CACHE_TIMEOUT_MS = 30 * 60 * 1000L // 30 min

@OptIn(ExperimentalPagingApi::class)
internal class MoviesRemoteMediator(
    private val apiService: TmdbApiService,
    private val db: MoviesDatabase,
) : RemoteMediator<Int, MovieEntity>() {
    private val movieDao = db.movieDao()
    private val remoteKeyDao = db.remoteKeyDao()

    override suspend fun initialize(): InitializeAction {
        val oldestFetch = movieDao.oldestFetchedAt() ?: return InitializeAction.LAUNCH_INITIAL_REFRESH
        return if (System.currentTimeMillis() - oldestFetch < CACHE_TIMEOUT_MS) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, MovieEntity>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> STARTING_PAGE
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    remoteKeyDao.remoteKey(lastItem.id)?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = apiService.popular(page)
            val endOfPagination = response.page >= response.totalPages

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    movieDao.clearAll()
                    remoteKeyDao.clearAll()
                }

                val fetchedAt = System.currentTimeMillis()
                val movies = response.results.map { it.toEntity(page, fetchedAt) }
                val keys = response.results.map { dto ->
                    RemoteKeyEntity(
                        movieId = dto.id,
                        prevPage = if (page == STARTING_PAGE) null else page - 1,
                        nextPage = if (endOfPagination) null else page + 1,
                        lastUpdated = fetchedAt,
                    )
                }
                movieDao.upsertAll(movies)
                remoteKeyDao.upsertAll(keys)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPagination)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
