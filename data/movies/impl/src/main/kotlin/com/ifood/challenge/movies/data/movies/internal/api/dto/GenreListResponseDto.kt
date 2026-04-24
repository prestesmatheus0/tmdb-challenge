package com.ifood.challenge.movies.data.movies.internal.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GenreListResponseDto(
    @SerialName("genres") val genres: List<GenreDto>,
)
