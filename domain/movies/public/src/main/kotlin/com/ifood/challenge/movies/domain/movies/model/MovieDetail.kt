package com.ifood.challenge.movies.domain.movies.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String,
    val voteAverage: Double,
    val releaseDate: String?,
    val runtimeMinutes: Int?,
    val tagline: String?,
    val genres: List<Genre>,
)
