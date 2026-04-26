package com.ifood.challenge.movies.data.movies.internal.mapper

import com.ifood.challenge.movies.core.database.entity.FavoriteEntity
import com.ifood.challenge.movies.core.database.entity.MovieDetailEntity
import com.ifood.challenge.movies.core.database.entity.MovieEntity
import com.ifood.challenge.movies.data.movies.internal.api.dto.GenreDto
import com.ifood.challenge.movies.data.movies.internal.api.dto.MovieDetailDto
import com.ifood.challenge.movies.data.movies.internal.api.dto.MovieDto
import com.ifood.challenge.movies.domain.movies.model.Genre
import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.domain.movies.model.MovieDetail

internal fun MovieDto.toEntity(page: Int, fetchedAt: Long = System.currentTimeMillis()): MovieEntity =
    MovieEntity(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        overview = overview,
        voteAverage = voteAverage,
        releaseDate = releaseDate,
        popularity = popularity,
        page = page,
        fetchedAt = fetchedAt,
    )

internal fun MovieEntity.toDomain(): Movie =
    Movie(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        overview = overview,
        voteAverage = voteAverage,
        releaseDate = releaseDate,
        popularity = popularity,
    )

internal fun MovieDetailDto.toEntity(fetchedAt: Long = System.currentTimeMillis()): MovieDetailEntity =
    MovieDetailEntity(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        overview = overview,
        voteAverage = voteAverage,
        releaseDate = releaseDate,
        runtimeMinutes = runtime,
        tagline = tagline,
        genresCsv = genres.joinToString(",") { it.id.toString() + ":" + it.name },
        fetchedAt = fetchedAt,
    )

internal fun MovieDetailEntity.toDomain(): MovieDetail =
    MovieDetail(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        overview = overview,
        voteAverage = voteAverage,
        releaseDate = releaseDate,
        runtimeMinutes = runtimeMinutes,
        tagline = tagline,
        genres = parseGenresCsv(genresCsv),
    )

internal fun MovieDto.toDomain(): Movie =
    Movie(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        overview = overview,
        voteAverage = voteAverage,
        releaseDate = releaseDate,
        popularity = popularity,
    )

internal fun GenreDto.toDomain(): Genre = Genre(id = id, name = name)

internal fun Movie.toFavoriteEntity(addedAt: Long = System.currentTimeMillis()): FavoriteEntity =
    FavoriteEntity(
        movieId = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        overview = overview,
        voteAverage = voteAverage,
        releaseDate = releaseDate,
        popularity = popularity,
        addedAt = addedAt,
    )

internal fun FavoriteEntity.toDomain(): Movie =
    Movie(
        id = movieId,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        overview = overview,
        voteAverage = voteAverage,
        releaseDate = releaseDate,
        popularity = popularity,
    )

private fun parseGenresCsv(csv: String): List<Genre> {
    if (csv.isBlank()) return emptyList()
    return csv.split(",").mapNotNull { entry ->
        val parts = entry.split(":")
        if (parts.size == 2) Genre(parts[0].toInt(), parts[1]) else null
    }
}
