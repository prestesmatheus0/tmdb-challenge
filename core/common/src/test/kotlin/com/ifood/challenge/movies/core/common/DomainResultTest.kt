package com.ifood.challenge.movies.core.common

import com.ifood.challenge.movies.core.common.result.DomainResult
import com.ifood.challenge.movies.core.common.result.asFailure
import com.ifood.challenge.movies.core.common.result.asSuccess
import com.ifood.challenge.movies.core.common.result.map
import com.ifood.challenge.movies.core.common.result.onFailure
import com.ifood.challenge.movies.core.common.result.onSuccess
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainResultTest {
    @Test
    fun `map transforms success value`() {
        val result: DomainResult<Int> = 2.asSuccess()
        val mapped = result.map { it * 3 }
        assertEquals(DomainResult.Success(6), mapped)
    }

    @Test
    fun `map preserves failure`() {
        val error = IllegalStateException("boom")
        val result: DomainResult<Int> = error.asFailure()
        val mapped = result.map { it * 3 }
        assertTrue(mapped is DomainResult.Failure)
        assertEquals(error, (mapped as DomainResult.Failure).error)
    }

    @Test
    fun `onSuccess fires only for success`() {
        var fired = 0
        ("ok".asSuccess()).onSuccess { fired++ }
        (RuntimeException().asFailure()).onSuccess { fired++ }
        assertEquals(1, fired)
    }

    @Test
    fun `onFailure fires only for failure`() {
        var fired = 0
        ("ok".asSuccess()).onFailure { fired++ }
        (RuntimeException().asFailure()).onFailure { fired++ }
        assertEquals(1, fired)
    }
}
