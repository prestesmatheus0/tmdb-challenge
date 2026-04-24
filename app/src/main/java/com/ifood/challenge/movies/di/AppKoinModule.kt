package com.ifood.challenge.movies.di

import com.ifood.challenge.movies.BuildConfig
import com.ifood.challenge.movies.core.network.NetworkConfig
import org.koin.dsl.module

val appKoinModule =
    module {
        single<NetworkConfig> {
            object : NetworkConfig {
                override val baseUrl: String = BuildConfig.TMDB_BASE_URL
                override val apiKey: String = BuildConfig.TMDB_API_KEY
                override val imageBaseUrl: String = BuildConfig.TMDB_IMAGE_BASE_URL
                override val language: String = "pt-BR"
                override val debug: Boolean = BuildConfig.DEBUG
            }
        }
    }
