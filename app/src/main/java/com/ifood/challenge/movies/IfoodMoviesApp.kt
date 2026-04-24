package com.ifood.challenge.movies

import android.app.Application
import com.ifood.challenge.movies.core.common.di.commonKoinModule
import com.ifood.challenge.movies.core.database.di.databaseKoinModule
import com.ifood.challenge.movies.core.network.di.networkKoinModule
import com.ifood.challenge.movies.di.appKoinModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class IfoodMoviesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.INFO else Level.ERROR)
            androidContext(this@IfoodMoviesApp)
            modules(
                appKoinModule,
                commonKoinModule,
                networkKoinModule,
                databaseKoinModule,
            )
        }
    }
}
