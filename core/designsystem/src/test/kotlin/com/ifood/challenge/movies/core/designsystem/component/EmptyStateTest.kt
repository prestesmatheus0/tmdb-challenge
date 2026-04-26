package com.ifood.challenge.movies.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EmptyStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysTitleAndDescription() {
        composeTestRule.setContent {
            EmptyState(
                icon = Icons.Filled.Movie,
                title = "Nada por aqui",
                description = "Adicione um filme aos favoritos",
            )
        }
        composeTestRule.onNodeWithText("Nada por aqui").assertIsDisplayed()
        composeTestRule.onNodeWithText("Adicione um filme aos favoritos").assertIsDisplayed()
    }
}
