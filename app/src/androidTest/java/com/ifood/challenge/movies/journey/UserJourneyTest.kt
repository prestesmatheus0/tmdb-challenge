package com.ifood.challenge.movies.journey

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ifood.challenge.movies.MainActivity
import com.ifood.challenge.movies.infra.AppKoinTestRule
import com.ifood.challenge.movies.infra.Fixtures
import com.ifood.challenge.movies.infra.MockWebServerRule
import com.ifood.challenge.movies.infra.waitUntilTextDisplayed
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserJourneyTest {
    private val mockWebServer = MockWebServerRule()
    private val koin = AppKoinTestRule(mockWebServer)
    private val compose = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val chain: RuleChain =
        RuleChain
            .outerRule(mockWebServer)
            .around(koin)
            .around(compose)

    @Test
    fun launch_showsPopularMovies() {
        compose.waitUntilTextDisplayed("Inception")
    }

    @Test
    fun clickMovie_navigatesToDetail() {
        compose.waitUntilTextDisplayed("Inception").performClick()
        compose.waitUntilTextDisplayed("Sinopse").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun favoriteFromDetail_increasesFavoritesChipCount() {
        compose.waitUntilTextDisplayed("Inception").performClick()
        compose.waitUntilTextDisplayed("Adicionar aos favoritos")
            .performScrollTo()
            .performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.waitUntilTextDisplayed("Favoritos · 1")
    }

    @Test
    fun searchToggle_filtersResults() {
        mockWebServer.route("/search/movie", Fixtures.searchResults("matrix"))

        compose.waitUntilTextDisplayed("Inception")
        compose.onNodeWithContentDescription("Buscar").performClick()
        compose.onNodeWithText("Buscar filmes…").performTextInput("matrix")
        compose.waitUntilTextDisplayed("matrix Match")
    }

    @Test
    fun genreFilter_callsDiscoverEndpoint() {
        compose.waitUntilTextDisplayed("Ação").performClick()
        compose.waitUntilTextDisplayed("Inception")

        assertTrue(
            "Expected /discover/movie request, got: ${mockWebServer.requestedPaths}",
            mockWebServer.hasRequestStartingWith("/discover/movie"),
        )
    }

    @Test
    fun errorState_thenRetry_recovers() {
        mockWebServer.routeError("/movie/popular", code = 500)

        compose.waitUntilTextDisplayed("Tentar novamente")

        mockWebServer.route("/movie/popular", Fixtures.popularPage())
        compose.onNodeWithText("Tentar novamente").performClick()

        compose.waitUntilTextDisplayed("Inception")
    }

    @Test
    fun emptyPopular_showsEmptyState() {
        mockWebServer.route("/movie/popular", Fixtures.emptyPage())

        compose.waitUntilTextDisplayed("Nenhum filme encontrado")
    }

    @Test
    fun searchNoResults_showsEmptyState() {
        mockWebServer.route("/search/movie", Fixtures.emptyPage())

        compose.waitUntilTextDisplayed("Inception")
        compose.onNodeWithContentDescription("Buscar").performClick()
        compose.onNodeWithText("Buscar filmes…").performTextInput("zzzqxx")
        compose.waitUntilTextDisplayed("Sem resultados")
    }

    @Test
    fun detailLoadFailure_showsRetry() {
        mockWebServer.routeError("/movie/", code = 500)

        compose.waitUntilTextDisplayed("Inception").performClick()
        compose.waitUntilTextDisplayed("Tentar novamente")
    }

    @Test
    fun favoritesEmpty_showsEmptyState() {
        compose.waitUntilTextDisplayed("Favoritos").performClick()
        compose.waitUntilTextDisplayed("Nenhum favorito ainda")
    }

    @Test
    fun genresFail_doesNotBlockMovieGrid() {
        mockWebServer.routeError("/genre/movie/list", code = 500)

        compose.waitUntilTextDisplayed("Inception")
    }
}
