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
            )
                .addMigrations(
                    MoviesDatabase.MIGRATION_1_2,
                    MoviesDatabase.MIGRATION_2_3,
                )
                // Cache tables (movies, movie_detail, remote_keys) are recoverable from network;
                // user-owned data (favorite_movies) has a real migration above.
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
        }
        single<MovieDao> { get<MoviesDatabase>().movieDao() }
        single<MovieDetailDao> { get<MoviesDatabase>().movieDetailDao() }
        single<FavoriteDao> { get<MoviesDatabase>().favoriteDao() }
        single<RemoteKeyDao> { get<MoviesDatabase>().remoteKeyDao() }
    }
