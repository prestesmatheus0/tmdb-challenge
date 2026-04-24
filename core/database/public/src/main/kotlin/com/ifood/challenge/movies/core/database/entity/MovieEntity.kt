package com.ifood.challenge.movies.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String,
    val voteAverage: Double,
    val releaseDate: String?,
    val popularity: Double,
    val page: Int,
    val fetchedAt: Long,
)
