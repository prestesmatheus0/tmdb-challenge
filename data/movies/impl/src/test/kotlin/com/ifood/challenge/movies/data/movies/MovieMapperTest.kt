package com.ifood.challenge.movies.data.movies

import com.ifood.challenge.movies.data.movies.internal.api.dto.GenreDto
import com.ifood.challenge.movies.data.movies.internal.api.dto.MovieDetailDto
import com.ifood.challenge.movies.data.movies.internal.api.dto.MovieDto
import com.ifood.challenge.movies.data.movies.internal.mapper.toDomain
import com.ifood.challenge.movies.data.movies.internal.mapper.toEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MovieMapperTest {
    private val movieDto = MovieDto(
        id = 1,
        title = "Inception",
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        overview = "A thief",
        voteAverage = 8.8,
        releaseDate = "2010-07-16",
        popularity = 100.0,
    )

    @Test
    fun `MovieDto toEntity mapeia todos os campos corretamente`() {
        val entity = movieDto.toEntity(page = 2, fetchedAt = 1000L)
        assertEquals(1, entity.id)
        assertEquals("Inception", entity.title)
        assertEquals("/poster.jpg", entity.posterPath)
        assertEquals(2, entity.page)
        assertEquals(1000L, entity.fetchedAt)
    }

    @Test
    fun `MovieEntity toDomain preserva campos do dominio`() {
        val entity = movieDto.toEntity(page = 1)
        val domain = entity.toDomain()
        assertEquals(movieDto.id, domain.id)
        assertEquals(movieDto.title, domain.title)
        assertEquals(movieDto.voteAverage, domain.voteAverage, 0.0)
    }

    @Test
    fun `MovieDto toDomain converte direto sem passar por entidade`() {
        val domain = movieDto.toDomain()
        assertEquals(movieDto.id, domain.id)
        assertEquals(movieDto.title, domain.title)
        assertEquals(movieDto.posterPath, domain.posterPath)
    }

    @Test
    fun `MovieDetailDto toEntity serializa genres como CSV`() {
        val dto = MovieDetailDto(
            id = 10,
            title = "Movie",
            genres = listOf(GenreDto(28, "Action"), GenreDto(12, "Adventure")),
        )
        val entity = dto.toEntity(fetchedAt = 0L)
        assertEquals("28:Action,12:Adventure", entity.genresCsv)
    }

    @Test
    fun `MovieDetailEntity toDomain desserializa genres do CSV`() {
        val dto = MovieDetailDto(
            id = 10,
            title = "Movie",
            genres = listOf(GenreDto(28, "Action"), GenreDto(12, "Adventure")),
        )
        val domain = dto.toEntity(fetchedAt = 0L).toDomain()
        assertEquals(2, domain.genres.size)
        assertEquals(28, domain.genres[0].id)
        assertEquals("Action", domain.genres[0].name)
    }

    @Test
    fun `MovieDetailEntity toDomain com genresCsv vazio retorna lista vazia`() {
        val dto = MovieDetailDto(id = 10, title = "Movie", genres = emptyList())
        val domain = dto.toEntity(fetchedAt = 0L).toDomain()
        assertEquals(emptyList<Any>(), domain.genres)
    }

    @Test
    fun `posterPath nulo e preservado`() {
        val dto = movieDto.copy(posterPath = null)
        assertNull(dto.toEntity(page = 1).posterPath)
        assertNull(dto.toDomain().posterPath)
    }
}
