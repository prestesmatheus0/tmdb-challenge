package com.ifood.challenge.movies.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class FavoriteEntity(
    @PrimaryKey val movieId: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String,
    val voteAverage: Double,
    val releaseDate: String?,
    val popularity: Double,
    val addedAt: Long,
)
