package com.ifood.challenge.movies.journey

import androidx.compose.ui.test.assertIsDisplayed
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
import com.ifood.challenge.movies.infra.DEFAULT_WAIT_MS
import com.ifood.challenge.movies.infra.createLazyAndroidComposeRule
import com.ifood.challenge.movies.infra.waitUntilTextDisplayed
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserJourneyTest {
    private val mockWebServer = MockWebServerRule()
    private val koin = AppKoinTestRule(mockWebServer)
    private val compose = createLazyAndroidComposeRule<MainActivity>()

    @get:Rule
    val chain: RuleChain =
        RuleChain
            .outerRule(mockWebServer)
            .around(koin)
            .around(compose)

    @Test
    fun launch_showsPopularMovies() {
        compose.launch()
        compose.composeRule.waitUntilTextDisplayed("Inception")
    }

    @Test
    fun clickMovie_navigatesToDetail() {
        compose.launch()
        compose.composeRule.waitUntilTextDisplayed("Inception").performClick()
        compose.composeRule.waitUntilTextDisplayed("Sinopse").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun favoriteFromDetail_increasesFavoritesChipCount() {
        compose.launch()
        compose.composeRule.waitUntilTextDisplayed("Inception").performClick()
        compose.composeRule.waitUntilTextDisplayed("Adicionar aos favoritos")
            .performScrollTo()
            .performClick()
        compose.composeRule.onNodeWithContentDescription("Voltar").performClick()
        compose.composeRule.waitUntilTextDisplayed("Favoritos · 1")
    }

    @Test
    fun searchToggle_filtersResults() {
        mockWebServer.route("/search/movie", Fixtures.searchResults("matrix"))
        compose.launch()

        compose.composeRule.waitUntilTextDisplayed("Inception")
        compose.composeRule.onNodeWithContentDescription("Buscar").performClick()
        compose.composeRule.onNodeWithText("Buscar filmes…").performTextInput("matrix")
        compose.composeRule.waitUntilTextDisplayed("matrix Match")
    }

    @Test
    fun genreFilter_callsDiscoverEndpoint() {
        compose.launch()
        compose.composeRule.waitUntilTextDisplayed("Ação").performClick()
        compose.composeRule.waitUntil(timeoutMillis = DEFAULT_WAIT_MS) {
            mockWebServer.hasRequestStartingWith("/discover/movie")
        }
    }

    @Test
    fun errorState_thenRetry_recovers() {
        mockWebServer.routeError("/movie/popular", code = 500)
        compose.launch()

        compose.composeRule.waitUntilTextDisplayed("Tentar novamente")

        mockWebServer.route("/movie/popular", Fixtures.popularPage())
        compose.composeRule.onNodeWithText("Tentar novamente").performClick()

        compose.composeRule.waitUntilTextDisplayed("Inception")
    }

    @Test
    fun emptyPopular_showsEmptyState() {
        mockWebServer.route("/movie/popular", Fixtures.emptyPage())
        compose.launch()

        compose.composeRule.waitUntilTextDisplayed("Nenhum filme encontrado")
    }

    @Test
    fun searchNoResults_showsEmptyState() {
        mockWebServer.route("/search/movie", Fixtures.emptyPage())
        compose.launch()

        compose.composeRule.waitUntilTextDisplayed("Inception")
        compose.composeRule.onNodeWithContentDescription("Buscar").performClick()
        compose.composeRule.onNodeWithText("Buscar filmes…").performTextInput("zzzqxx")
        compose.composeRule.waitUntilTextDisplayed("Sem resultados")
    }

    @Test
    fun detailLoadFailure_showsRetry() {
        mockWebServer.routeError("/movie/", code = 500)
        compose.launch()

        compose.composeRule.waitUntilTextDisplayed("Inception").performClick()
        compose.composeRule.waitUntilTextDisplayed("Tentar novamente")
    }

    @Test
    fun favoritesEmpty_showsEmptyState() {
        compose.launch()
        compose.composeRule.waitUntilTextDisplayed("Favoritos").performClick()
        compose.composeRule.waitUntilTextDisplayed("Nenhum favorito ainda")
    }

    @Test
    fun genresFail_doesNotBlockMovieGrid() {
        mockWebServer.routeError("/genre/movie/list", code = 500)
        compose.launch()

        compose.composeRule.waitUntilTextDisplayed("Inception")
    }
}
