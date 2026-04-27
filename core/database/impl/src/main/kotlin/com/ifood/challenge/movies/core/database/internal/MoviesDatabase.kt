package com.ifood.challenge.movies.core.database.internal

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 3,
    exportSchema = false,
)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    abstract fun movieDetailDao(): MovieDetailDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object {
        const val NAME = "ifood_movies.db"

        /**
         * v1 → v2: enrich `favorite_movies` with backdrop_path, overview, release_date, popularity.
         *
         * SQLite ALTER TABLE only supports ADD COLUMN, so we add the new columns with
         * sensible defaults. Existing rows preserve their movieId/title/posterPath/voteAverage/addedAt.
         */
        val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE favorite_movies ADD COLUMN backdropPath TEXT DEFAULT NULL")
                    db.execSQL("ALTER TABLE favorite_movies ADD COLUMN overview TEXT NOT NULL DEFAULT ''")
                    db.execSQL("ALTER TABLE favorite_movies ADD COLUMN releaseDate TEXT DEFAULT NULL")
                    db.execSQL("ALTER TABLE favorite_movies ADD COLUMN popularity REAL NOT NULL DEFAULT 0.0")
                }
            }

        /** v2 → v3: add popularity to movie_details so favorites kept after Detail-favorite preserve rank. */
        val MIGRATION_2_3 =
            object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE movie_details ADD COLUMN popularity REAL NOT NULL DEFAULT 0.0")
                }
            }
    }
}
