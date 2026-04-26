package com.ifood.challenge.movies.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ifood.challenge.movies.core.database.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY page ASC, popularity DESC")
    fun pagingSource(): PagingSource<Int, MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    fun observeById(id: Int): Flow<MovieEntity?>

    @Query("SELECT MAX(page) FROM movies")
    suspend fun lastLoadedPage(): Int?

    @Query("SELECT MIN(fetchedAt) FROM movies")
    suspend fun oldestFetchedAt(): Long?

    @Upsert
    suspend fun upsertAll(movies: List<MovieEntity>)

    @Query("DELETE FROM movies")
    suspend fun clearAll()
}
