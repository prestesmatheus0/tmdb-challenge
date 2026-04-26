package com.ifood.challenge.movies.infra

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.rules.ExternalResource

/**
 * Spins up a local [MockWebServer] for each test.
 *
 * Routes are matched by URL prefix and respond with canned JSON. Use [enqueueResponse]
 * for one-off responses; the dispatcher fallback returns 404 for unmatched paths.
 */
class MockWebServerRule : ExternalResource() {

    val server: MockWebServer = MockWebServer()
    val baseUrl: String get() = server.url("/").toString()

    private val routes = mutableMapOf<String, () -> MockResponse>()

    override fun before() {
        routes.clear()
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path.orEmpty()
                val handler = routes.entries.firstOrNull { (prefix, _) -> path.startsWith(prefix) }
                return handler?.value?.invoke() ?: MockResponse().setResponseCode(404)
            }
        }
        server.start()
    }

    override fun after() {
        server.shutdown()
    }

    /** Register a JSON response for any path matching [pathPrefix]. */
    fun route(pathPrefix: String, body: String, code: Int = 200) {
        routes[pathPrefix] = {
            MockResponse()
                .setResponseCode(code)
                .setHeader("Content-Type", "application/json")
                .setBody(body)
        }
    }

    /** Replace any handler for [pathPrefix] with an error response. */
    fun routeError(pathPrefix: String, code: Int = 500) {
        routes[pathPrefix] = { MockResponse().setResponseCode(code) }
    }
}
