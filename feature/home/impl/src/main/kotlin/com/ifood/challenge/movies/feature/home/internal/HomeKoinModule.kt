package com.ifood.challenge.movies.feature.home.internal

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeKoinModule =
    module {
        viewModelOf(::HomeViewModel)
    }
