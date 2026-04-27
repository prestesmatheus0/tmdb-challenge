package com.ifood.challenge.movies.data.movies.internal.api

import com.ifood.challenge.movies.data.movies.internal.api.dto.GenreListResponseDto
import com.ifood.challenge.movies.data.movies.internal.api.dto.MovieDetailDto
import com.ifood.challenge.movies.data.movies.internal.api.dto.MovieListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface TmdbApiService {
    @GET("movie/popular")
    suspend fun popular(
        @Query("page") page: Int,
    ): MovieListResponseDto

    @GET("movie/now_playing")
    suspend fun nowPlaying(
        @Query("page") page: Int,
    ): MovieListResponseDto

    @GET("discover/movie")
    suspend fun discover(
        @Query("page") page: Int,
        @Query("with_genres") genreId: Int,
    ): MovieListResponseDto

    @GET("movie/{id}")
    suspend fun movieDetail(
        @Path("id") movieId: Int,
    ): MovieDetailDto

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int,
    ): MovieListResponseDto

    @GET("genre/movie/list")
    suspend fun genres(): GenreListResponseDto
}
