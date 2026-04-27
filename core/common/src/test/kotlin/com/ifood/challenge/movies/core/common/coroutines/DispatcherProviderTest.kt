package com.ifood.challenge.movies.core.common.coroutines

import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertSame
import org.junit.Test

class DispatcherProviderTest {
    @Test
    fun defaultProvider_exposesStandardDispatchers() {
        val provider = DefaultDispatcherProvider()
        assertSame(Dispatchers.Main, provider.main)
        assertSame(Dispatchers.IO, provider.io)
        assertSame(Dispatchers.Default, provider.default)
    }
}
