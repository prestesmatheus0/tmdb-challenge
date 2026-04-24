package com.ifood.challenge.movies.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ifood.challenge.movies.core.database.entity.RemoteKeyEntity

@Dao
interface RemoteKeyDao {
    @Query("SELECT * FROM remote_keys WHERE movieId = :movieId LIMIT 1")
    suspend fun remoteKey(movieId: Int): RemoteKeyEntity?

    @Query("SELECT MIN(lastUpdated) FROM remote_keys")
    suspend fun oldestLastUpdated(): Long?

    @Upsert
    suspend fun upsertAll(keys: List<RemoteKeyEntity>)

    @Query("DELETE FROM remote_keys")
    suspend fun clearAll()
}
