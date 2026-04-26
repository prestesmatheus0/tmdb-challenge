package com.ifood.challenge.movies.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_details")
data class MovieDetailEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String,
    val voteAverage: Double,
    val releaseDate: String?,
    val runtimeMinutes: Int?,
    val tagline: String?,
    val genresCsv: String,
    val fetchedAt: Long,
)
