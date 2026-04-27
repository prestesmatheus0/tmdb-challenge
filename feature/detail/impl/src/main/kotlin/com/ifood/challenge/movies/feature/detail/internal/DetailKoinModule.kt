package com.ifood.challenge.movies.feature.detail.internal

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val detailKoinModule =
    module {
        viewModel {
            DetailViewModel(
                savedStateHandle = get(),
                fetchDetail = get(),
                observeDetail = get(),
                observeIsFavorite = get(),
                setFavorite = get(),
            )
        }
    }
