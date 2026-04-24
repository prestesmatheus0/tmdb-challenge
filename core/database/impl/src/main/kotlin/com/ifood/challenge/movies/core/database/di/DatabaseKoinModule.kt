package com.ifood.challenge.movies.core.database.di

import androidx.room.Room
import com.ifood.challenge.movies.core.database.dao.FavoriteDao
import com.ifood.challenge.movies.core.database.dao.MovieDao
import com.ifood.challenge.movies.core.database.dao.MovieDetailDao
import com.ifood.challenge.movies.core.database.dao.RemoteKeyDao
import com.ifood.challenge.movies.core.database.internal.MoviesDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseKoinModule =
    module {
        single<MoviesDatabase> {
            Room.databaseBuilder(
                androidContext(),
                MoviesDatabase::class.java,
                MoviesDatabase.NAME,
            ).fallbackToDestructiveMigration().build()
        }
        single<MovieDao> { get<MoviesDatabase>().movieDao() }
        single<MovieDetailDao> { get<MoviesDatabase>().movieDetailDao() }
        single<FavoriteDao> { get<MoviesDatabase>().favoriteDao() }
        single<RemoteKeyDao> { get<MoviesDatabase>().remoteKeyDao() }
    }
