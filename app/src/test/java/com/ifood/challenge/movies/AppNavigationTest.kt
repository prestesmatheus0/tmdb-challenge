package com.ifood.challenge.movies

import com.ifood.challenge.movies.feature.detail.DetailRoute
import com.ifood.challenge.movies.feature.home.HomeRoute
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class AppNavigationTest {
    @Test
    fun homeRoute_isSerializableSingleton() {
        val json = Json.encodeToString(HomeRoute.serializer(), HomeRoute)
        val decoded = Json.decodeFromString(HomeRoute.serializer(), json)

        assertSame(HomeRoute, decoded)
    }

    @Test
    fun detailRoute_preservesMovieId() {
        val route = DetailRoute(movieId = 27205)
        assertEquals(27205, route.movieId)
    }

    @Test
    fun detailRoute_serializableRoundTrip() {
        val original = DetailRoute(movieId = 12345)
        val json = Json.encodeToString(DetailRoute.serializer(), original)
        val decoded = Json.decodeFromString(DetailRoute.serializer(), json)
        assertEquals(original, decoded)
        assertEquals(12345, decoded.movieId)
    }

    @Test
    fun detailRoute_dataClassEquality() {
        val a = DetailRoute(movieId = 1)
        val b = DetailRoute(movieId = 1)
        val c = DetailRoute(movieId = 2)

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assert(a != c)
    }
}
