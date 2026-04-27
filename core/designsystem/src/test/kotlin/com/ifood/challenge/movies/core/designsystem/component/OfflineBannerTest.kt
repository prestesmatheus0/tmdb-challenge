package com.ifood.challenge.movies.core.designsystem.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OfflineBannerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysOfflineMessage() {
        composeTestRule.setContent { OfflineBanner() }
        composeTestRule.onNodeWithText("Você está offline. Exibindo dados em cache.").assertIsDisplayed()
    }

    @Test
    fun rootHasTestTag() {
        composeTestRule.setContent { OfflineBanner() }
        composeTestRule.onNodeWithTag(OfflineBannerTestTags.root).assertIsDisplayed()
    }
}
