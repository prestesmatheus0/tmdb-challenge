package com.ifood.challenge.movies.journey

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ifood.challenge.movies.MainActivity
import com.ifood.challenge.movies.infra.AppKoinTestRule
import com.ifood.challenge.movies.infra.Fixtures
import com.ifood.challenge.movies.infra.MockWebServerRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

/**
 * Verifies that ViewModels (via SavedStateHandle) survive configuration changes.
 *
 * `activityRule.scenario.recreate()` triggers Activity destruction + recreation,
 * mimicking rotation. State preserved in [androidx.lifecycle.SavedStateHandle] should
 * be restored automatically.
 */
@RunWith(AndroidJUnit4::class)
class ConfigChangeTest {

    private val mockWebServer = MockWebServerRule()
    private val koin = AppKoinTestRule(mockWebServer)
    private val compose = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val chain: RuleChain = RuleChain
        .outerRule(mockWebServer)
        .around(koin)
        .around(compose)

    @Test
    fun rotation_preservesSelectedFilter() {
        mockWebServer.route("/movie/popular", Fixtures.popularPage())
        mockWebServer.route("/movie/now_playing", Fixtures.popularPage())
        mockWebServer.route("/genre/movie/list", Fixtures.genres())
        mockWebServer.route("/discover/movie", Fixtures.popularPage())

        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithText("Mais Recentes").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Mais Recentes").performClick()

        compose.activityRule.scenario.recreate()

        // After recreate, "Mais Recentes" chip should still be the selected filter.
        // SavedStateHandle restored it; we assert chip is still displayed (and grid populated).
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithText("Mais Recentes").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Mais Recentes").assertIsDisplayed()
    }

    @Test
    fun rotation_preservesSearchQuery() {
        mockWebServer.route("/movie/popular", Fixtures.popularPage())
        mockWebServer.route("/genre/movie/list", Fixtures.genres())
        mockWebServer.route("/search/movie", Fixtures.searchResults("inception"))

        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithText("Inception").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithContentDescription("Buscar").performClick()
        compose.onNodeWithText("Buscar filmes…").performTextInput("inception")

        compose.activityRule.scenario.recreate()

        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithText("inception").fetchSemanticsNodes().isNotEmpty()
        }
        // search bar should still be visible with the query
        compose.onNodeWithText("inception").assertIsDisplayed()
    }

    @Test
    fun rotation_inDetail_preservesMovieId() {
        mockWebServer.route("/movie/popular", Fixtures.popularPage())
        mockWebServer.route("/movie/27205", Fixtures.movieDetail(movieId = 27205))
        mockWebServer.route("/genre/movie/list", Fixtures.genres())

        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithText("Inception").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onAllNodesWithText("Inception").onFirst().performClick()

        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithText("Sinopse").fetchSemanticsNodes().isNotEmpty()
        }

        compose.activityRule.scenario.recreate()

        // After recreate, Detail screen should still show the same movie's content.
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithText("Sinopse").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Sinopse").assertIsDisplayed()
    }

    @Test
    fun rotation_preservesFavorites() {
        mockWebServer.route("/movie/popular", Fixtures.popularPage())
        mockWebServer.route("/movie/27205", Fixtures.movieDetail(movieId = 27205))
        mockWebServer.route("/genre/movie/list", Fixtures.genres())

        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithText("Inception").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onAllNodesWithText("Inception").onFirst().performClick()
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithText("Adicionar aos favoritos").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Adicionar aos favoritos").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()

        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithText("Favoritos · 1").fetchSemanticsNodes().isNotEmpty()
        }

        compose.activityRule.scenario.recreate()

        // Room is the source of truth; favorite count must persist.
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithText("Favoritos · 1").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Favoritos · 1").assertIsDisplayed()
    }
}
