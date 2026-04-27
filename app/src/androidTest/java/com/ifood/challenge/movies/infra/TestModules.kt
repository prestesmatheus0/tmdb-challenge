package com.ifood.challenge.movies.infra

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.ifood.challenge.movies.core.database.dao.FavoriteDao
import com.ifood.challenge.movies.core.database.dao.MovieDao
import com.ifood.challenge.movies.core.database.dao.MovieDetailDao
import com.ifood.challenge.movies.core.database.dao.RemoteKeyDao
import com.ifood.challenge.movies.core.database.internal.MoviesDatabase
import com.ifood.challenge.movies.core.network.NetworkConfig
import org.koin.core.module.Module
import org.koin.dsl.module

fun testNetworkModule(baseUrl: String): Module =
    module {
        single<NetworkConfig> {
            object : NetworkConfig {
                override val baseUrl: String = baseUrl
                override val apiKey: String = "test-token"
                override val imageBaseUrl: String = baseUrl
                override val language: String = "pt-BR"
                override val debug: Boolean = true
            }
        }
    }

fun testDatabaseModule(): Module =
    module {
        single<MoviesDatabase> {
            Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext,
                MoviesDatabase::class.java,
            )
                .allowMainThreadQueries()
                .build()
        }
        single<MovieDao> { get<MoviesDatabase>().movieDao() }
        single<MovieDetailDao> { get<MoviesDatabase>().movieDetailDao() }
        single<FavoriteDao> { get<MoviesDatabase>().favoriteDao() }
        single<RemoteKeyDao> { get<MoviesDatabase>().remoteKeyDao() }
    }
