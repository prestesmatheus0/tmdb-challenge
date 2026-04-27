package com.ifood.challenge.movies.journey

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ifood.challenge.movies.MainActivity
import com.ifood.challenge.movies.infra.AppKoinTestRule
import com.ifood.challenge.movies.infra.Fixtures
import com.ifood.challenge.movies.infra.MockWebServerRule
import com.ifood.challenge.movies.infra.waitUntilTextDisplayed
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

/**
 * Configuration change survival via [androidx.lifecycle.SavedStateHandle].
 *
 * `activityRule.scenario.recreate()` mimics rotation by destroying and reinstantiating the
 * Activity. ViewModels backed by SavedStateHandle restore their state automatically.
 *
 * Follows the Android Compose testing guide patterns
 * (https://developer.android.com/develop/ui/compose/testing): semantic finders + waitUntil
 * idle synchronization.
 */
@RunWith(AndroidJUnit4::class)
class ConfigChangeTest {
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
    fun rotation_preservesSelectedFilter() {
        mockWebServer.route("/movie/popular", Fixtures.popularPage())
        mockWebServer.route("/movie/now_playing", Fixtures.popularPage())
        mockWebServer.route("/genre/movie/list", Fixtures.genres())
        mockWebServer.route("/discover/movie", Fixtures.popularPage())

        compose.waitUntilTextDisplayed("Mais Recentes").performClick()

        compose.activityRule.scenario.recreate()

        // SavedStateHandle restored: chip remains visible after rebuild.
        compose.waitUntilTextDisplayed("Mais Recentes")
    }

    @Test
    fun rotation_preservesSearchQuery() {
        mockWebServer.route("/movie/popular", Fixtures.popularPage())
        mockWebServer.route("/genre/movie/list", Fixtures.genres())
        mockWebServer.route("/search/movie", Fixtures.searchResults("inception"))

        compose.waitUntilTextDisplayed("Inception")
        compose.onNodeWithContentDescription("Buscar").performClick()
        compose.onNodeWithText("Buscar filmes…").performTextInput("inception")

        compose.activityRule.scenario.recreate()

        compose.waitUntilTextDisplayed("inception")
    }

    @Test
    fun rotation_inDetail_preservesMovieId() {
        mockWebServer.route("/movie/popular", Fixtures.popularPage())
        mockWebServer.route("/movie/27205", Fixtures.movieDetail(movieId = 27205))
        mockWebServer.route("/genre/movie/list", Fixtures.genres())

        compose.waitUntilTextDisplayed("Inception").performClick()
        compose.waitUntilTextDisplayed("Sinopse")

        compose.activityRule.scenario.recreate()

        compose.waitUntilTextDisplayed("Sinopse")
    }

    @Test
    fun rotation_preservesFavorites() {
        mockWebServer.route("/movie/popular", Fixtures.popularPage())
        mockWebServer.route("/movie/27205", Fixtures.movieDetail(movieId = 27205))
        mockWebServer.route("/genre/movie/list", Fixtures.genres())

        compose.waitUntilTextDisplayed("Inception").performClick()
        compose.waitUntilTextDisplayed("Adicionar aos favoritos").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.waitUntilTextDisplayed("Favoritos · 1")

        compose.activityRule.scenario.recreate()

        // Room (DB-backed) survives Activity recreation independent of SavedStateHandle.
        compose.waitUntilTextDisplayed("Favoritos · 1")
    }
}
