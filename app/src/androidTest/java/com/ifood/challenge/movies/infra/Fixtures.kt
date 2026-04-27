package com.ifood.challenge.movies.infra

/** Canned TMDB-shaped JSON responses for instrumentation tests. */
object Fixtures {
    fun popularPage(
        page: Int = 1,
        totalPages: Int = 1,
    ): String =
        """
        {
          "page": $page,
          "total_pages": $totalPages,
          "results": [
            {
              "id": 27205,
              "title": "Inception",
              "overview": "A thief who steals corporate secrets.",
              "poster_path": "/inception.jpg",
              "backdrop_path": "/inception_bd.jpg",
              "vote_average": 8.8,
              "release_date": "2010-07-16",
              "popularity": 100.0
            },
            {
              "id": 12345,
              "title": "Interstellar",
              "overview": "A team of explorers travel through a wormhole.",
              "poster_path": "/inter.jpg",
              "backdrop_path": "/inter_bd.jpg",
              "vote_average": 8.6,
              "release_date": "2014-11-07",
              "popularity": 90.0
            }
          ]
        }
        """.trimIndent()

    fun emptyPage(): String =
        """
        {"page": 1, "total_pages": 1, "results": []}
        """.trimIndent()

    fun searchResults(query: String): String =
        """
        {
          "page": 1,
          "total_pages": 1,
          "results": [
            {
              "id": 99999,
              "title": "$query Match",
              "overview": "Result for query: $query",
              "poster_path": null,
              "backdrop_path": null,
              "vote_average": 7.0,
              "release_date": "2020-01-01",
              "popularity": 50.0
            }
          ]
        }
        """.trimIndent()

    fun movieDetail(movieId: Int = 27205): String =
        """
        {
          "id": $movieId,
          "title": "Inception",
          "overview": "A thief who steals corporate secrets through dream-sharing technology.",
          "poster_path": "/inception.jpg",
          "backdrop_path": "/inception_bd.jpg",
          "vote_average": 8.8,
          "release_date": "2010-07-16",
          "runtime": 148,
          "tagline": "Your mind is the scene of the crime",
          "genres": [
            {"id": 28, "name": "Ação"},
            {"id": 878, "name": "Ficção Científica"}
          ]
        }
        """.trimIndent()

    fun genres(): String =
        """
        {
          "genres": [
            {"id": 28, "name": "Ação"},
            {"id": 12, "name": "Aventura"},
            {"id": 16, "name": "Animação"}
          ]
        }
        """.trimIndent()
}
