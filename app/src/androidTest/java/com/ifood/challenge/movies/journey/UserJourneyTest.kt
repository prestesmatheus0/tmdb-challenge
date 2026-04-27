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

/**
 * End-to-end user journeys: real Activity, real Koin (with overrides), real Room (in-memory),
 * fake network via MockWebServer.
 *
 * Patterns follow https://developer.android.com/develop/ui/compose/testing :
 *  - `createAndroidComposeRule<MainActivity>()` to launch the real Activity
 *  - Finders: `onNodeWithText`, `onNodeWithContentDescription` (semantics-based)
 *  - Actions: `performClick`, `performTextInput`, `performScrollTo`
 *  - Synchronization: `waitUntil { … }` (wrapped in `waitUntilTextDisplayed` helper)
 *  - Assertions: `assertIsDisplayed()` after every wait
 */
@RunWith(AndroidJUnit4::class)
class UserJourneyTest {

    private val mockWebServer = MockWebServerRule()
    private val koin = AppKoinTestRule(mockWebServer)
    private val compose = createAndroidComposeRule<MainActivity>()

    // Order matters: MockWebServer starts first (Koin reads baseUrl), Activity launches last.
    @get:Rule
    val chain: RuleChain = RuleChain
        .outerRule(mockWebServer)
        .around(koin)
        .around(compose)

    @Test
    fun launch_showsPopularMovies() {
        mockWebServer.route("/movie/popular", Fixtures.popularPage())
        mockWebServer.route("/genre/movie/list", Fixtures.genres())

        compose.waitUntilTextDisplayed("Inception")
    }

    @Test
    fun clickMovie_navigatesToDetail() {
        mockWebServer.route("/movie/popular", Fixtures.popularPage())
        mockWebServer.route("/movie/27205", Fixtures.movieDetail(movieId = 27205))
        mockWebServer.route("/genre/movie/list", Fixtures.genres())

        compose.waitUntilTextDisplayed("Inception").performClick()
        compose.waitUntilTextDisplayed("Sinopse").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun favoriteFromDetail_increasesFavoritesChipCount() {
        mockWebServer.route("/movie/popular", Fixtures.popularPage())
        mockWebServer.route("/movie/27205", Fixtures.movieDetail(movieId = 27205))
        mockWebServer.route("/genre/movie/list", Fixtures.genres())

        compose.waitUntilTextDisplayed("Inception").performClick()
        compose.waitUntilTextDisplayed("Adicionar aos favoritos")
            .performScrollTo()
            .performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.waitUntilTextDisplayed("Favoritos · 1")
    }

    @Test
    fun searchToggle_filtersResults() {
        mockWebServer.route("/movie/popular", Fixtures.popularPage())
        mockWebServer.route("/genre/movie/list", Fixtures.genres())
        mockWebServer.route("/search/movie", Fixtures.searchResults("matrix"))

        compose.waitUntilTextDisplayed("Inception")
        compose.onNodeWithContentDescription("Buscar").performClick()
        compose.onNodeWithText("Buscar filmes…").performTextInput("matrix")
        compose.waitUntilTextDisplayed("matrix Match")
    }

    @Test
    fun genreFilter_callsDiscoverEndpoint() {
        mockWebServer.route("/movie/popular", Fixtures.popularPage())
        mockWebServer.route("/genre/movie/list", Fixtures.genres())
        mockWebServer.route("/discover/movie", Fixtures.popularPage())

        compose.waitUntilTextDisplayed("Ação").performClick()
        compose.waitUntilTextDisplayed("Inception")

        // Verify the right endpoint was actually hit (not just the grid populated).
        assertTrue(
            "Expected /discover/movie request, got: ${mockWebServer.requestedPaths}",
            mockWebServer.hasRequestStartingWith("/discover/movie"),
        )
    }

    @Test
    fun errorState_thenRetry_recovers() {
        mockWebServer.route("/genre/movie/list", Fixtures.genres())
        mockWebServer.routeError("/movie/popular", code = 500)

        compose.waitUntilTextDisplayed("Tentar novamente")

        // Swap to success then retry
        mockWebServer.route("/movie/popular", Fixtures.popularPage())
        compose.onNodeWithText("Tentar novamente").performClick()

        compose.waitUntilTextDisplayed("Inception")
    }
}
