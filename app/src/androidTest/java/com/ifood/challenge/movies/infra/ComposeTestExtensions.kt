package com.ifood.challenge.movies.infra

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule

const val DEFAULT_WAIT_MS: Long = 5_000

fun <A : androidx.activity.ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.waitUntilTextDisplayed(
    text: String,
    timeoutMillis: Long = DEFAULT_WAIT_MS,
): SemanticsNodeInteraction {
    waitUntil(timeoutMillis = timeoutMillis) {
        onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
    }
    return onNodeWithText(text).assertIsDisplayed()
}

fun <A : androidx.activity.ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.waitUntilAnyTextDisplayed(
    text: String,
    timeoutMillis: Long = DEFAULT_WAIT_MS,
) {
    waitUntil(timeoutMillis = timeoutMillis) {
        onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
    }
}
