package com.ifood.challenge.movies.feature.detail.internal

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val detailKoinModule =
    module {
        viewModelOf(::DetailViewModel)
    }
