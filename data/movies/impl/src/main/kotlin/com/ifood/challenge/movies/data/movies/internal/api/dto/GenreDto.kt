package com.ifood.challenge.movies.data.movies.internal.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GenreDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
)
