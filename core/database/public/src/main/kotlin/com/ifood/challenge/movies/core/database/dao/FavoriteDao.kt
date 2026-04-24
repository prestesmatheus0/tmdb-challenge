package com.ifood.challenge.movies.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ifood.challenge.movies.core.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_movies ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE movieId = :movieId)")
    fun observeIsFavorite(movieId: Int): Flow<Boolean>

    @Upsert
    suspend fun upsert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorite_movies WHERE movieId = :movieId")
    suspend fun delete(movieId: Int)
}
