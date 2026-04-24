package com.ifood.challenge.movies.core.common.di

import com.ifood.challenge.movies.core.common.coroutines.DefaultDispatcherProvider
import com.ifood.challenge.movies.core.common.coroutines.DispatcherProvider
import com.ifood.challenge.movies.core.common.network.AndroidConnectivityObserver
import com.ifood.challenge.movies.core.common.network.ConnectivityObserver
import org.koin.dsl.module

val commonKoinModule =
    module {
        single<DispatcherProvider> { DefaultDispatcherProvider() }
        single<ConnectivityObserver> { AndroidConnectivityObserver(get()) }
    }
