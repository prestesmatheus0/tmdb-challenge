package com.ifood.challenge.movies.domain.movies.model

data class Movie(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String,
    val voteAverage: Double,
    val releaseDate: String?,
    val popularity: Double,
)
