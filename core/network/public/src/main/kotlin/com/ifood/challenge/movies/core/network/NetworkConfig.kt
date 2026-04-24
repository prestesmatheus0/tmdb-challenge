package com.ifood.challenge.movies.core.network

interface NetworkConfig {
    val baseUrl: String
    val apiKey: String
    val imageBaseUrl: String
    val language: String
    val debug: Boolean
}
