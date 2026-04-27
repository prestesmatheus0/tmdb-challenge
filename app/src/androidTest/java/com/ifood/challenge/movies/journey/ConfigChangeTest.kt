package com.ifood.challenge.movies.journey

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ifood.challenge.movies.MainActivity
import com.ifood.challenge.movies.infra.AppKoinTestRule
import com.ifood.challenge.movies.infra.Fixtures
import com.ifood.challenge.movies.infra.MockWebServerRule
import com.ifood.challenge.movies.infra.createLazyAndroidComposeRule
import com.ifood.challenge.movies.infra.waitUntilTextDisplayed
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConfigChangeTest {
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
    fun rotation_preservesSelectedFilter() {
        compose.launch()
        compose.composeRule.waitUntilTextDisplayed("Mais Recentes").performClick()

        compose.recreate()

        compose.composeRule.waitUntilTextDisplayed("Mais Recentes")
    }

    @Test
    fun rotation_preservesSearchQuery() {
        mockWebServer.route("/search/movie", Fixtures.searchResults("inception"))
        compose.launch()

        compose.composeRule.waitUntilTextDisplayed("Inception")
        compose.composeRule.onNodeWithContentDescription("Buscar").performClick()
        compose.composeRule.onNodeWithText("Buscar filmes…").performTextInput("inception")

        compose.recreate()

        compose.composeRule.waitUntilTextDisplayed("inception")
    }

    @Test
    fun rotation_inDetail_preservesMovieId() {
        compose.launch()
        compose.composeRule.waitUntilTextDisplayed("Inception").performClick()
        compose.composeRule.waitUntilTextDisplayed("Sinopse")

        compose.recreate()

        compose.composeRule.waitUntilTextDisplayed("Sinopse")
    }

    @Test
    fun rotation_preservesFavorites() {
        compose.launch()
        compose.composeRule.waitUntilTextDisplayed("Inception").performClick()
        compose.composeRule.waitUntilTextDisplayed("Adicionar aos favoritos").performClick()
        compose.composeRule.onNodeWithContentDescription("Voltar").performClick()
        compose.composeRule.waitUntilTextDisplayed("Favoritos · 1")

        compose.recreate()

        compose.composeRule.waitUntilTextDisplayed("Favoritos · 1")
    }
}
