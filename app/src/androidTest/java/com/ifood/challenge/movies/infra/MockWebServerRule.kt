package com.ifood.challenge.movies.infra

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.rules.ExternalResource

class MockWebServerRule : ExternalResource() {
    val server: MockWebServer = MockWebServer()
    val baseUrl: String get() = server.url("/").toString()

    private val routes = java.util.concurrent.ConcurrentHashMap<String, () -> MockResponse>()
    private val recordedPaths = mutableListOf<String>()

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

    private fun registerDefaultRoutes() {
        route("/movie/popular", Fixtures.popularPage())
        route("/movie/now_playing", Fixtures.popularPage())
        route("/discover/movie", Fixtures.popularPage())
        route("/search/movie", Fixtures.searchResults("default"))
        route("/genre/movie/list", Fixtures.genres())

        route("/movie/", Fixtures.movieDetail())
    }

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

    fun routeError(
        pathPrefix: String,
        code: Int = 500,
    ) {
        routes[pathPrefix] = { MockResponse().setResponseCode(code) }
    }

    fun hasRequestStartingWith(pathPrefix: String): Boolean =
        synchronized(recordedPaths) { recordedPaths.any { it.startsWith(pathPrefix) } }
}
