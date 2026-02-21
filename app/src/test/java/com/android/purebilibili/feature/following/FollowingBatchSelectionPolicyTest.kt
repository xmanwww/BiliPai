package com.android.purebilibili.feature.following

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FollowingBatchSelectionPolicyTest {

    @Test
    fun `toggle selection should add mid when not selected`() {
        val selected = toggleFollowingSelection(current = setOf(1L, 2L), mid = 3L)
        assertEquals(setOf(1L, 2L, 3L), selected)
    }

    @Test
    fun `toggle selection should remove mid when selected`() {
        val selected = toggleFollowingSelection(current = setOf(1L, 2L), mid = 2L)
        assertEquals(setOf(1L), selected)
    }

    @Test
    fun `select all should pick every visible user when not fully selected`() {
        val resolved = resolveFollowingSelectAll(
            visibleMids = listOf(10L, 20L, 30L),
            currentSelected = setOf(10L)
        )
        assertEquals(setOf(10L, 20L, 30L), resolved)
    }

    @Test
    fun `select all should clear visible users when already fully selected`() {
        val resolved = resolveFollowingSelectAll(
            visibleMids = listOf(10L, 20L, 30L),
            currentSelected = setOf(10L, 20L, 30L, 99L)
        )
        assertEquals(setOf(99L), resolved)
    }

    @Test
    fun `batch unfollow message should include success and failure`() {
        val message = buildBatchUnfollowResultMessage(successCount = 7, failedCount = 2)
        assertTrue(message.contains("7"))
        assertTrue(message.contains("2"))
    }
}
