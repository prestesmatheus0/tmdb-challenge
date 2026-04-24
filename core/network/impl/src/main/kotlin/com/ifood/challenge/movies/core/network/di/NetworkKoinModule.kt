package com.ifood.challenge.movies.core.network.di

import com.ifood.challenge.movies.core.network.ImageUrlBuilder
import com.ifood.challenge.movies.core.network.NetworkConfig
import com.ifood.challenge.movies.core.network.internal.DefaultImageUrlBuilder
import com.ifood.challenge.movies.core.network.internal.RetrofitFactory
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit

val networkKoinModule =
    module {
        single<Json> { RetrofitFactory.json() }
        single<OkHttpClient> { RetrofitFactory.okHttp(get<NetworkConfig>()) }
        single<Retrofit> {
            RetrofitFactory.retrofit(
                client = get(),
                json = get(),
                config = get(),
            )
        }
        single<ImageUrlBuilder> { DefaultImageUrlBuilder(get()) }
    }
