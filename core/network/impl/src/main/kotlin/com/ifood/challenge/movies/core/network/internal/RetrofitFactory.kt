package com.ifood.challenge.movies.core.network.internal

import com.ifood.challenge.movies.core.network.NetworkConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

internal object RetrofitFactory {
    fun okHttp(config: NetworkConfig): OkHttpClient {
        val builder =
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(AuthInterceptor(config))

        if (config.debug) {
            builder.addInterceptor(RedactingLogInterceptor())
        }

        return builder.build()
    }

    fun retrofit(
        client: OkHttpClient,
        json: Json,
        config: NetworkConfig,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(client)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType()),
            )
            .build()

    fun json(): Json =
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            coerceInputValues = true
        }
}
