package com.ifood.challenge.movies.core.designsystem.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ErrorStateTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun networkVariant_showsCorrectTitle() {
        composeTestRule.setContent {
            ErrorState(variant = ErrorVariant.Network, onRetry = {})
        }
        composeTestRule.onNodeWithText("Sem conexão").assertIsDisplayed()
    }

    @Test
    fun timeoutVariant_showsCorrectTitle() {
        composeTestRule.setContent {
            ErrorState(variant = ErrorVariant.Timeout, onRetry = {})
        }
        composeTestRule.onNodeWithText("Tempo esgotado").assertIsDisplayed()
    }

    @Test
    fun serverVariant_showsCorrectTitle() {
        composeTestRule.setContent {
            ErrorState(variant = ErrorVariant.Server, onRetry = {})
        }
        composeTestRule.onNodeWithText("Servidor indisponível").assertIsDisplayed()
    }

    @Test
    fun genericVariant_showsCorrectTitle() {
        composeTestRule.setContent {
            ErrorState(variant = ErrorVariant.Generic, onRetry = {})
        }
        composeTestRule.onNodeWithText("Algo deu errado").assertIsDisplayed()
    }

    @Test
    fun retryButton_callsCallback() {
        var clicked = false
        composeTestRule.setContent {
            ErrorState(variant = ErrorVariant.Network, onRetry = { clicked = true })
        }
        composeTestRule.onNodeWithText("Tentar novamente").performClick()
        assertTrue(clicked)
    }
}
