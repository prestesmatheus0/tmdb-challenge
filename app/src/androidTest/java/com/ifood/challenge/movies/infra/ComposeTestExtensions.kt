package com.ifood.challenge.movies.infra

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import org.junit.rules.TestRule

const val DEFAULT_WAIT_MS: Long = 5_000

fun <R : TestRule, A : androidx.activity.ComponentActivity> AndroidComposeTestRule<R, A>.waitUntilTextDisplayed(
    text: String,
    timeoutMillis: Long = DEFAULT_WAIT_MS,
): SemanticsNodeInteraction {
    waitUntil(timeoutMillis = timeoutMillis) {
        onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
    }
    return onNodeWithText(text).assertIsDisplayed()
}

fun <R : TestRule, A : androidx.activity.ComponentActivity> AndroidComposeTestRule<R, A>.waitUntilAnyTextDisplayed(
    text: String,
    timeoutMillis: Long = DEFAULT_WAIT_MS,
) {
    waitUntil(timeoutMillis = timeoutMillis) {
        onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
    }
}
