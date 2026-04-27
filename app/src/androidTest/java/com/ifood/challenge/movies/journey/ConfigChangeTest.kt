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
        compose.waitUntilTextDisplayed("Mais Recentes").performClick()

        compose.activityRule.scenario.recreate()

        compose.waitUntilTextDisplayed("Mais Recentes")
    }

    @Test
    fun rotation_preservesSearchQuery() {
        mockWebServer.route("/search/movie", Fixtures.searchResults("inception"))

        compose.waitUntilTextDisplayed("Inception")
        compose.onNodeWithContentDescription("Buscar").performClick()
        compose.onNodeWithText("Buscar filmes…").performTextInput("inception")

        compose.activityRule.scenario.recreate()

        compose.waitUntilTextDisplayed("inception")
    }

    @Test
    fun rotation_inDetail_preservesMovieId() {
        compose.waitUntilTextDisplayed("Inception").performClick()
        compose.waitUntilTextDisplayed("Sinopse")

        compose.activityRule.scenario.recreate()

        compose.waitUntilTextDisplayed("Sinopse")
    }

    @Test
    fun rotation_preservesFavorites() {
        compose.waitUntilTextDisplayed("Inception").performClick()
        compose.waitUntilTextDisplayed("Adicionar aos favoritos").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.waitUntilTextDisplayed("Favoritos · 1")

        compose.activityRule.scenario.recreate()

        compose.waitUntilTextDisplayed("Favoritos · 1")
    }
}
