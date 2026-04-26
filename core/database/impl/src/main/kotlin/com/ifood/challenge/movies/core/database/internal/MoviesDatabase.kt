package com.ifood.challenge.movies.core.database.internal

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ifood.challenge.movies.core.database.dao.FavoriteDao
import com.ifood.challenge.movies.core.database.dao.MovieDao
import com.ifood.challenge.movies.core.database.dao.MovieDetailDao
import com.ifood.challenge.movies.core.database.dao.RemoteKeyDao
import com.ifood.challenge.movies.core.database.entity.FavoriteEntity
import com.ifood.challenge.movies.core.database.entity.MovieDetailEntity
import com.ifood.challenge.movies.core.database.entity.MovieEntity
import com.ifood.challenge.movies.core.database.entity.RemoteKeyEntity

@Database(
    entities = [
        MovieEntity::class,
        MovieDetailEntity::class,
        FavoriteEntity::class,
        RemoteKeyEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    abstract fun movieDetailDao(): MovieDetailDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object {
        const val NAME = "ifood_movies.db"
    }
}
