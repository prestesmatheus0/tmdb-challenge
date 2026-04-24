package com.ifood.challenge.movies.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val movieId: Int,
    val prevPage: Int?,
    val nextPage: Int?,
    val lastUpdated: Long,
)
