package com.ifood.challenge.movies.infra

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.core.app.ActivityScenario
import org.junit.rules.ExternalResource
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Delays Activity launch until [launch] is called, so tests can
 * configure mock routes before the Activity makes network requests.
 */
class LazyAndroidComposeRule<A : ComponentActivity>(
    private val activityClass: Class<A>,
) : ExternalResource() {

    @Volatile
    private var scenario: ActivityScenario<A>? = null

    val composeRule: AndroidComposeTestRule<ExternalResource, A> =
        AndroidComposeTestRule(activityRule = object : ExternalResource() {}) {
            var act: A? = null
            checkNotNull(scenario) { "Call launch() first" }.onActivity { act = it }
            checkNotNull(act)
        }

    override fun apply(base: Statement, description: Description): Statement =
        composeRule.apply(
            object : Statement() {
                override fun evaluate() {
                    try { base.evaluate() } finally { scenario?.close(); scenario = null }
                }
            },
            description,
        )

    fun launch() {
        check(scenario == null) { "Already launched" }
        scenario = ActivityScenario.launch(activityClass)
        composeRule.waitForIdle()
    }

    fun recreate() {
        checkNotNull(scenario) { "Not launched" }.recreate()
        composeRule.waitForIdle()
    }
}

inline fun <reified A : ComponentActivity> createLazyAndroidComposeRule() =
    LazyAndroidComposeRule(A::class.java)
