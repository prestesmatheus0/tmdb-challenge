package com.ifood.challenge.movies.core.designsystem.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.assertEquals

@RunWith(RobolectricTestRunner::class)
class FilterChipRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val chips = listOf(
        MovieFilterChip(key = "popular", label = "Popular"),
        MovieFilterChip(key = "favorites", label = "Favoritos"),
        MovieFilterChip(key = "now", label = "Mais Recentes"),
    )

    @Test
    fun rendersAllChips() {
        composeTestRule.setContent {
            FilterChipRow(chips = chips, selected = "popular", onSelect = {})
        }
        composeTestRule.onNodeWithText("Popular").assertIsDisplayed()
        composeTestRule.onNodeWithText("Favoritos").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mais Recentes").assertIsDisplayed()
    }

    @Test
    fun click_callsOnSelectWithKey() {
        var selected: String? = null
        composeTestRule.setContent {
            FilterChipRow(chips = chips, selected = "popular", onSelect = { selected = it })
        }
        composeTestRule.onNodeWithText("Favoritos").performClick()
        assertEquals("favorites", selected)
    }

    @Test
    fun rootHasTestTag() {
        composeTestRule.setContent {
            FilterChipRow(chips = chips, selected = "popular", onSelect = {})
        }
        composeTestRule.onNodeWithTag(FilterChipRowTestTags.root).assertIsDisplayed()
    }
}
