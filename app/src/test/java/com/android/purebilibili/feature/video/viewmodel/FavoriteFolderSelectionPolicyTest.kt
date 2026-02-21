package com.android.purebilibili.feature.video.viewmodel

import kotlin.test.Test
import kotlin.test.assertEquals

class FavoriteFolderSelectionPolicyTest {

    @Test
    fun `should compute add and remove folders when selection changed`() {
        val mutation = resolveFavoriteFolderMutation(
            original = setOf(10L, 20L),
            selected = setOf(20L, 30L)
        )

        assertEquals(setOf(30L), mutation.addFolderIds)
        assertEquals(setOf(10L), mutation.removeFolderIds)
    }

    @Test
    fun `should produce empty mutation when selection unchanged`() {
        val mutation = resolveFavoriteFolderMutation(
            original = setOf(100L, 200L),
            selected = setOf(100L, 200L)
        )

        assertEquals(emptySet(), mutation.addFolderIds)
        assertEquals(emptySet(), mutation.removeFolderIds)
    }

    @Test
    fun `should support removing all folders`() {
        val mutation = resolveFavoriteFolderMutation(
            original = setOf(1L, 2L),
            selected = emptySet()
        )

        assertEquals(emptySet(), mutation.addFolderIds)
        assertEquals(setOf(1L, 2L), mutation.removeFolderIds)
    }
}
