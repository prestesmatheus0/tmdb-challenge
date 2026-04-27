package com.ifood.challenge.movies.infra

import androidx.test.platform.app.InstrumentationRegistry
import com.ifood.challenge.movies.core.common.di.commonKoinModule
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

class AppKoinTestRule(
    private val mockWebServer: MockWebServerRule,
) : ExternalResource() {
    override fun before() {
        if (GlobalContext.getOrNull() != null) stopKoin()

        startKoin {
            androidContext(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
            modules(
                commonKoinModule,
                networkKoinModule,
                dataMoviesKoinModule,
                domainMoviesKoinModule,
                homeKoinModule,
                detailKoinModule,
                testDatabaseModule(),
                appKoinModule,
                testNetworkModule(mockWebServer.baseUrl),
            )
        }
    }

    override fun after() {
        stopKoin()
    }
}
