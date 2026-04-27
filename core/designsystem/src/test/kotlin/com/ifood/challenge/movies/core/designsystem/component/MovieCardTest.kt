package com.ifood.challenge.movies.core.designsystem.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MovieCardTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysTitleAndRating() {
        composeTestRule.setContent {
            MovieCard(
                title = "Inception",
                posterUrl = null,
                rating = 8.8,
                isFavorite = false,
                onClick = {},
                onFavoriteToggle = {},
            )
        }
        composeTestRule.onNodeWithText("Inception").assertIsDisplayed()
        composeTestRule.onNodeWithText("8.8").assertIsDisplayed()
    }

    @Test
    fun click_callsOnClick() {
        var clicked = false
        composeTestRule.setContent {
            MovieCard(
                title = "Inception",
                posterUrl = null,
                rating = 8.8,
                isFavorite = false,
                onClick = { clicked = true },
                onFavoriteToggle = {},
            )
        }
        composeTestRule.onNodeWithTag(MovieCardTestTags.root("Inception")).performClick()
        assertTrue(clicked)
    }

    @Test
    fun favoriteButton_callsOnToggle() {
        var toggled = false
        composeTestRule.setContent {
            MovieCard(
                title = "Inception",
                posterUrl = null,
                rating = 8.8,
                isFavorite = false,
                onClick = {},
                onFavoriteToggle = { toggled = true },
            )
        }
        composeTestRule.onNodeWithTag(MovieCardTestTags.favorite("Inception")).performClick()
        assertTrue(toggled)
    }

    @Test
    fun notFavorite_showsAddDescription() {
        composeTestRule.setContent {
            MovieCard(
                title = "Inception",
                posterUrl = null,
                rating = 8.8,
                isFavorite = false,
                onClick = {},
                onFavoriteToggle = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("Favoritar Inception").assertIsDisplayed()
    }

    @Test
    fun isFavorite_showsRemoveDescription() {
        composeTestRule.setContent {
            MovieCard(
                title = "Inception",
                posterUrl = null,
                rating = 8.8,
                isFavorite = true,
                onClick = {},
                onFavoriteToggle = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("Remover Inception dos favoritos").assertIsDisplayed()
    }
}
