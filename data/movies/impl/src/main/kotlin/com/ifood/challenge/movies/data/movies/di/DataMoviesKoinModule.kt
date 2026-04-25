package com.ifood.challenge.movies.data.movies.di

import com.ifood.challenge.movies.data.movies.internal.api.TmdbApiService
import com.ifood.challenge.movies.data.movies.internal.repository.MoviesRepositoryImpl
import com.ifood.challenge.movies.domain.movies.repository.MoviesRepository
import org.koin.dsl.module
import retrofit2.Retrofit

val dataMoviesKoinModule =
    module {
        single<TmdbApiService> { get<Retrofit>().create(TmdbApiService::class.java) }
        single<MoviesRepository> {
            MoviesRepositoryImpl(
                apiService = get(),
                db = get(),
                movieDao = get(),
                movieDetailDao = get(),
                favoriteDao = get(),
                dispatchers = get(),
            )
        }
    }
