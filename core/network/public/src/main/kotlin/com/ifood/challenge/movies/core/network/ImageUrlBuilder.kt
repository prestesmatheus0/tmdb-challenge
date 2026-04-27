package com.ifood.challenge.movies.core.network

interface ImageUrlBuilder {
    fun poster(
        path: String?,
        size: PosterSize = PosterSize.W500,
    ): String?

    fun backdrop(
        path: String?,
        size: BackdropSize = BackdropSize.W1280,
    ): String?
}

enum class PosterSize(val segment: String) {
    W185("w185"),
    W342("w342"),
    W500("w500"),
    W780("w780"),
    Original("original"),
}

enum class BackdropSize(val segment: String) {
    W300("w300"),
    W780("w780"),
    W1280("w1280"),
    Original("original"),
}
