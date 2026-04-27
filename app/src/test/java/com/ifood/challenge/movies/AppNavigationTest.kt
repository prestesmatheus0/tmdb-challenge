package com.ifood.challenge.movies

import com.ifood.challenge.movies.feature.detail.DetailRoute
import com.ifood.challenge.movies.feature.home.HomeRoute
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Type-safe Navigation 3 routes: data class + @Serializable.
 * Verifies routes round-trip through Kotlinx Serialization (used by Compose nav under the hood)
 * and that arguments are preserved.
 */
class AppNavigationTest {
    @Test
    fun homeRoute_isSingletonObject() {
        // HomeRoute has no args, must remain stable across navigation
        assertNotNull(HomeRoute)
        assertEquals(HomeRoute, HomeRoute)
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
