package com.ifood.challenge.movies.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ifood.challenge.movies.core.database.entity.MovieDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDetailDao {
    @Query("SELECT * FROM movie_details WHERE id = :id LIMIT 1")
    fun observe(id: Int): Flow<MovieDetailEntity?>

    @Query("SELECT * FROM movie_details WHERE id = :id LIMIT 1")
    suspend fun get(id: Int): MovieDetailEntity?

    @Upsert
    suspend fun upsert(detail: MovieDetailEntity)
}
