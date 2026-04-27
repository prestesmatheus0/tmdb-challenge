package com.ifood.challenge.movies.infra

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.rules.ExternalResource

/**
 * Spins up a local [MockWebServer] for each test.
 *
 * Routes are matched by URL prefix and respond with canned JSON. Use [route] / [routeError]
 * to override; the dispatcher returns 404 for unmatched paths.
 *
 * IMPORTANT: default routes for common TMDB endpoints are pre-registered in [before], because
 * `createAndroidComposeRule<MainActivity>()` launches the Activity (and the ViewModel's first
 * paging request) BEFORE the test body runs. Without defaults, the first request hits 404 and
 * the grid never recovers (Paging does not auto-retry). Tests that need different bodies just
 * call `route(...)` again — the map is overwritten.
 */
class MockWebServerRule : ExternalResource() {
    val server: MockWebServer = MockWebServer()
    val baseUrl: String get() = server.url("/").toString()

    // Accessed from both the test thread (route()/routeError()) and OkHttp dispatcher thread.
    // ConcurrentHashMap provides safe concurrent reads + writes without explicit synchronization.
    private val routes = java.util.concurrent.ConcurrentHashMap<String, () -> MockResponse>()
    private val recordedPaths = mutableListOf<String>()

    /** Snapshot of every path the server has dispatched, in order. Reset per test. */
    val requestedPaths: List<String> get() = recordedPaths.toList()

    override fun before() {
        routes.clear()
        recordedPaths.clear()
        server.dispatcher =
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    val path = request.path.orEmpty()
                    synchronized(recordedPaths) { recordedPaths.add(path) }
                    val handler = routes.entries.firstOrNull { (prefix, _) -> path.startsWith(prefix) }
                    return handler?.value?.invoke() ?: MockResponse().setResponseCode(404)
                }
            }
        server.start()
        registerDefaultRoutes()
    }

    override fun after() {
        server.shutdown()
    }

    /**
     * Pre-register success responses for every TMDB endpoint the app calls on cold start.
     * Tests can still override any of these via [route] / [routeError] to exercise edge cases.
     */
    private fun registerDefaultRoutes() {
        route("/movie/popular", Fixtures.popularPage())
        route("/movie/now_playing", Fixtures.popularPage())
        route("/discover/movie", Fixtures.popularPage())
        route("/search/movie", Fixtures.searchResults("default"))
        route("/genre/movie/list", Fixtures.genres())
        // /movie/{id} default — used when test navigates to detail without overriding
        route("/movie/", Fixtures.movieDetail())
    }

    /** Register a JSON response for any path matching [pathPrefix]. */
    fun route(
        pathPrefix: String,
        body: String,
        code: Int = 200,
    ) {
        routes[pathPrefix] = {
            MockResponse()
                .setResponseCode(code)
                .setHeader("Content-Type", "application/json")
                .setBody(body)
        }
    }

    /** Replace any handler for [pathPrefix] with an error response. */
    fun routeError(
        pathPrefix: String,
        code: Int = 500,
    ) {
        routes[pathPrefix] = { MockResponse().setResponseCode(code) }
    }

    /** True if any recorded request started with [pathPrefix]. */
    fun hasRequestStartingWith(pathPrefix: String): Boolean =
        synchronized(recordedPaths) { recordedPaths.any { it.startsWith(pathPrefix) } }
}
