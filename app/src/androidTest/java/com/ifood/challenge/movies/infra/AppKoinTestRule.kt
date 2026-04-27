package com.ifood.challenge.movies.infra

import androidx.test.platform.app.InstrumentationRegistry
import com.ifood.challenge.movies.core.common.di.commonKoinModule
import com.ifood.challenge.movies.core.database.di.databaseKoinModule
import com.ifood.challenge.movies.core.network.di.networkKoinModule
import com.ifood.challenge.movies.data.movies.di.dataMoviesKoinModule
import com.ifood.challenge.movies.di.appKoinModule
import com.ifood.challenge.movies.domain.movies.di.domainMoviesKoinModule
import com.ifood.challenge.movies.feature.detail.internal.detailKoinModule
import com.ifood.challenge.movies.feature.home.internal.homeKoinModule
import org.junit.rules.ExternalResource
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

/**
 * Stops the production Koin graph started by [com.ifood.challenge.movies.IfoodMoviesApp]
 * and rebuilds it with overrides for [NetworkConfig] (→ MockWebServer) and
 * [MoviesDatabase] (→ in-memory Room).
 *
 * Order with other rules: declare AFTER [MockWebServerRule] so its base URL is available.
 */
class AppKoinTestRule(
    private val mockWebServer: MockWebServerRule,
) : ExternalResource() {
    override fun before() {
        // tear down whatever Application started
        if (GlobalContext.getOrNull() != null) stopKoin()

        startKoin {
            androidContext(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
            modules(
                appKoinModule,
                commonKoinModule,
                networkKoinModule,
                databaseKoinModule,
                dataMoviesKoinModule,
                domainMoviesKoinModule,
                homeKoinModule,
                detailKoinModule,
                // overrides MUST come last
                testNetworkModule(mockWebServer.baseUrl),
                testDatabaseModule(),
            )
        }
    }

    override fun after() {
        stopKoin()
    }
}
