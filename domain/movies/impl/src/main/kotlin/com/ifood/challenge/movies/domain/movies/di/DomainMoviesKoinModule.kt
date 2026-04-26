package com.ifood.challenge.movies.domain.movies.di

import com.ifood.challenge.movies.domain.movies.internal.FetchMovieDetailUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetGenresUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetMoviesByGenreUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetNowPlayingMoviesUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetPopularMoviesUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetIsFavoriteUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetMovieDetailUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetMoviesByQueryUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetFavoriteIdsUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetFavoriteMoviesUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.SetFavoriteUseCaseImpl
import com.ifood.challenge.movies.domain.movies.usecase.FetchMovieDetailUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetGenresUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetMoviesByGenreUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetNowPlayingMoviesUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetPopularMoviesUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetIsFavoriteUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetMovieDetailUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetMoviesByQueryUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetFavoriteIdsUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetFavoriteMoviesUseCase
import com.ifood.challenge.movies.domain.movies.usecase.SetFavoriteUseCase
import org.koin.dsl.module

val domainMoviesKoinModule =
    module {
        factory<GetPopularMoviesUseCase> { GetPopularMoviesUseCaseImpl(get()) }
        factory<GetNowPlayingMoviesUseCase> { GetNowPlayingMoviesUseCaseImpl(get()) }
        factory<GetMoviesByGenreUseCase> { GetMoviesByGenreUseCaseImpl(get()) }
        factory<GetGenresUseCase> { GetGenresUseCaseImpl(get()) }
        factory<GetMovieDetailUseCase> { GetMovieDetailUseCaseImpl(get()) }
        factory<FetchMovieDetailUseCase> { FetchMovieDetailUseCaseImpl(get()) }
        factory<GetIsFavoriteUseCase> { GetIsFavoriteUseCaseImpl(get()) }
        factory<SetFavoriteUseCase> { SetFavoriteUseCaseImpl(get()) }
        factory<GetMoviesByQueryUseCase> { GetMoviesByQueryUseCaseImpl(get()) }
        factory<GetFavoriteIdsUseCase> { GetFavoriteIdsUseCaseImpl(get()) }
        factory<GetFavoriteMoviesUseCase> { GetFavoriteMoviesUseCaseImpl(get()) }
    }
