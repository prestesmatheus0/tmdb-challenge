package com.ifood.challenge.movies.core.network.internal

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

internal class RedactingLogInterceptor(
    private val tag: String = "MoviesHttp",
    private val redactKeys: Set<String> = setOf("api_key"),
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val started = System.nanoTime()

        Log.d(tag, "--> ${request.method} ${redact(request.url.toString())}")

        val response = chain.proceed(request)
        val durationMs = (System.nanoTime() - started) / 1_000_000
        Log.d(
            tag,
            "<-- ${response.code} ${response.message} " +
                "${redact(response.request.url.toString())} (${durationMs}ms)",
        )
        return response
    }

    private fun redact(url: String): String =
        redactKeys.fold(url) { acc, key ->
            acc.replace(Regex("($key=)[^&]+"), "$1***")
        }
}
