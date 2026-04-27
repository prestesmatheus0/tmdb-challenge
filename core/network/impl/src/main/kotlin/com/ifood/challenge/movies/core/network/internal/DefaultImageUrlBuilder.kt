package com.ifood.challenge.movies.core.network.internal

import com.ifood.challenge.movies.core.network.BackdropSize
import com.ifood.challenge.movies.core.network.ImageUrlBuilder
import com.ifood.challenge.movies.core.network.NetworkConfig
import com.ifood.challenge.movies.core.network.PosterSize

internal class DefaultImageUrlBuilder(
    private val config: NetworkConfig,
) : ImageUrlBuilder {
    override fun poster(
        path: String?,
        size: PosterSize,
    ): String? = path?.let { config.imageBaseUrl.trimEnd('/') + "/" + size.segment + ensureLeadingSlash(it) }

    override fun backdrop(
        path: String?,
        size: BackdropSize,
    ): String? = path?.let { config.imageBaseUrl.trimEnd('/') + "/" + size.segment + ensureLeadingSlash(it) }

    private fun ensureLeadingSlash(raw: String): String = if (raw.startsWith("/")) raw else "/$raw"
}
