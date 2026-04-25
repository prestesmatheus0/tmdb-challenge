package com.ifood.challenge.movies.domain.movies.di

import com.ifood.challenge.movies.domain.movies.internal.FetchMovieDetailUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetGenresUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetMoviesByGenreUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetPopularMoviesUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.ObserveIsFavoriteUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.ObserveMovieDetailUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetMoviesByQueryUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.ObserveFavoriteIdsUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.GetFavoriteMoviesUseCaseImpl
import com.ifood.challenge.movies.domain.movies.internal.SetFavoriteUseCaseImpl
import com.ifood.challenge.movies.domain.movies.usecase.FetchMovieDetailUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetGenresUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetMoviesByGenreUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetPopularMoviesUseCase
import com.ifood.challenge.movies.domain.movies.usecase.ObserveIsFavoriteUseCase
import com.ifood.challenge.movies.domain.movies.usecase.ObserveMovieDetailUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetMoviesByQueryUseCase
import com.ifood.challenge.movies.domain.movies.usecase.ObserveFavoriteIdsUseCase
import com.ifood.challenge.movies.domain.movies.usecase.GetFavoriteMoviesUseCase
import com.ifood.challenge.movies.domain.movies.usecase.SetFavoriteUseCase
import org.koin.dsl.module

val domainMoviesKoinModule =
    module {
        factory<GetPopularMoviesUseCase> { GetPopularMoviesUseCaseImpl(get()) }
        factory<GetMoviesByGenreUseCase> { GetMoviesByGenreUseCaseImpl(get()) }
        factory<GetGenresUseCase> { GetGenresUseCaseImpl(get()) }
        factory<ObserveMovieDetailUseCase> { ObserveMovieDetailUseCaseImpl(get()) }
        factory<FetchMovieDetailUseCase> { FetchMovieDetailUseCaseImpl(get()) }
        factory<ObserveIsFavoriteUseCase> { ObserveIsFavoriteUseCaseImpl(get()) }
        factory<SetFavoriteUseCase> { SetFavoriteUseCaseImpl(get()) }
        factory<GetMoviesByQueryUseCase> { GetMoviesByQueryUseCaseImpl(get()) }
        factory<ObserveFavoriteIdsUseCase> { ObserveFavoriteIdsUseCaseImpl(get()) }
        factory<GetFavoriteMoviesUseCase> { GetFavoriteMoviesUseCaseImpl(get()) }
    }
