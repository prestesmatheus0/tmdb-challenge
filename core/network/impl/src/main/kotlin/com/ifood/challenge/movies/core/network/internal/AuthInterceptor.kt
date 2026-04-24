package com.ifood.challenge.movies.core.network.internal

import com.ifood.challenge.movies.core.network.NetworkConfig
import okhttp3.Interceptor
import okhttp3.Response

internal class AuthInterceptor(
    private val config: NetworkConfig,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url =
            original.url.newBuilder()
                .addQueryParameter("api_key", config.apiKey)
                .addQueryParameter("language", config.language)
                .build()
        val request =
            original.newBuilder()
                .url(url)
                .header("Accept", "application/json")
                .build()
        return chain.proceed(request)
    }
}
