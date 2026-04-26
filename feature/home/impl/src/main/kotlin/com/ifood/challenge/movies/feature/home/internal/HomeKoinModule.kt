package com.ifood.challenge.movies.feature.home.internal

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeKoinModule =
    module {
        viewModel {
            HomeViewModel(
                savedStateHandle = get(),
                getPopularMovies = get(),
                getNowPlayingMovies = get(),
                getMoviesByGenre = get(),
                getMoviesByQuery = get(),
                getGenres = get(),
                setFavorite = get(),
                getFavoriteMovies = get(),
                observeFavoriteIds = get(),
                connectivityObserver = get(),
            )
        }
    }
