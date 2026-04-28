package com.ifood.challenge.movies.feature.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.ifood.challenge.movies.core.network.BackdropSize
import com.ifood.challenge.movies.core.network.ImageUrlBuilder
import com.ifood.challenge.movies.core.network.PosterSize
import com.ifood.challenge.movies.domain.movies.model.Genre
import com.ifood.challenge.movies.domain.movies.model.MovieDetail
import com.ifood.challenge.movies.feature.detail.internal.DetailAction
import com.ifood.challenge.movies.feature.detail.internal.DetailScreen
import com.ifood.challenge.movies.feature.detail.internal.DetailUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class DetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeImageUrlBuilder = object : ImageUrlBuilder {
        override fun poster(path: String?, size: PosterSize) = ""

        override fun backdrop(path: String?, size: BackdropSize) = ""
    }

    private fun setContent(
        uiState: DetailUiState,
        onBack: () -> Unit = {},
        onAction: (DetailAction) -> Unit = {},
    ) {
        composeTestRule.setContent {
            DetailScreen(
                uiState = uiState,
                onBack = onBack,
                onAction = onAction,
                imageUrlBuilder = fakeImageUrlBuilder,
            )
        }
    }

    @Test
    fun loading_showsBackButton() {
        setContent(uiState = DetailUiState.Loading)
        composeTestRule.onNodeWithContentDescription("Voltar").assertIsDisplayed()
    }

    @Test
    fun error_showsRetryButton() {
        setContent(uiState = DetailUiState.Error)
        composeTestRule.onNodeWithText("Tentar novamente").assertIsDisplayed()
    }

    @Test
    fun error_retryButton_callsOnRetry() {
        var retried = false
        setContent(
            uiState = DetailUiState.Error,
            onAction = { if (it is DetailAction.Retry) retried = true },
        )
        composeTestRule.onNodeWithText("Tentar novamente").performClick()
        assertTrue(retried)
    }

    @Test
    fun success_showsTitle() {
        setContent(uiState = DetailUiState.Success(TEST_DETAIL, isFavorite = false))

        val nodes = composeTestRule.onAllNodesWithText("Inception")
        nodes[0].assertExists()
    }

    @Test
    fun success_showsOverview() {
        setContent(uiState = DetailUiState.Success(TEST_DETAIL, isFavorite = false))
        composeTestRule.onNodeWithText("A thief who steals corporate secrets").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun success_showsYear() {
        setContent(uiState = DetailUiState.Success(TEST_DETAIL, isFavorite = false))
        composeTestRule.onNodeWithText("2010").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun success_showsGenreChips() {
        setContent(uiState = DetailUiState.Success(TEST_DETAIL, isFavorite = false))
        composeTestRule.onNodeWithText("Action").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Sci-Fi").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun success_notFavorite_showsAddButton() {
        setContent(uiState = DetailUiState.Success(TEST_DETAIL, isFavorite = false))
        composeTestRule.onNodeWithText("Adicionar aos favoritos").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun success_isFavorite_showsRemoveButton() {
        setContent(uiState = DetailUiState.Success(TEST_DETAIL, isFavorite = true))
        composeTestRule.onNodeWithText("Remover dos favoritos").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun success_favoriteButton_callsOnFavoriteToggle() {
        var toggled = false
        setContent(
            uiState = DetailUiState.Success(TEST_DETAIL, isFavorite = false),
            onAction = { if (it is DetailAction.FavoriteToggle) toggled = true },
        )
        composeTestRule.onNodeWithText("Adicionar aos favoritos").performScrollTo().performClick()
        assertTrue(toggled)
    }

    @Test
    fun backButton_callsOnBack() {
        var backed = false
        setContent(
            uiState = DetailUiState.Success(TEST_DETAIL, isFavorite = false),
            onBack = { backed = true },
        )
        composeTestRule.onNodeWithContentDescription("Voltar").performClick()
        assertTrue(backed)
    }

    @Test
    fun success_showsTagline() {
        setContent(uiState = DetailUiState.Success(TEST_DETAIL, isFavorite = false))
        composeTestRule.onNodeWithText("Your mind is the scene of the crime").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun success_showsRuntime() {
        setContent(uiState = DetailUiState.Success(TEST_DETAIL, isFavorite = false))
        composeTestRule.onNodeWithText("· 148min").performScrollTo().assertIsDisplayed()
    }

    companion object {
        private val TEST_DETAIL = MovieDetail(
            id = 42,
            title = "Inception",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            overview = "A thief who steals corporate secrets",
            voteAverage = 8.8,
            releaseDate = "2010-07-16",
            runtimeMinutes = 148,
            tagline = "Your mind is the scene of the crime",
            popularity = 0.0,
            genres = listOf(Genre(28, "Action"), Genre(878, "Sci-Fi")),
        )
    }
}
