package com.ifood.challenge.movies.infra

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule

/** Reasonable upper bound for any single async UI transition (network → state → composition). */
const val DEFAULT_WAIT_MS: Long = 5_000

/**
 * Suspends the test until at least one node with [text] enters the composition,
 * then returns the node interaction asserted as displayed.
 *
 * Mirrors the official Compose testing guide's recommended pattern:
 * `composeTestRule.waitUntil { … } ; composeTestRule.onNode … assertIsDisplayed()`.
 */
fun <A : androidx.activity.ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.waitUntilTextDisplayed(
    text: String,
    timeoutMillis: Long = DEFAULT_WAIT_MS,
): SemanticsNodeInteraction {
    waitUntil(timeoutMillis = timeoutMillis) {
        onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
    }
    return onNodeWithText(text).assertIsDisplayed()
}

/** Same as [waitUntilTextDisplayed] but for the first match (when the same text appears in multiple nodes). */
fun <A : androidx.activity.ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.waitUntilAnyTextDisplayed(
    text: String,
    timeoutMillis: Long = DEFAULT_WAIT_MS,
) {
    waitUntil(timeoutMillis = timeoutMillis) {
        onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
    }
}
