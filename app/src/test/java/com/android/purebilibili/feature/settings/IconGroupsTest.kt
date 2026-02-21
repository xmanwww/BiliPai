package com.android.purebilibili.feature.settings

import kotlin.test.Test
import kotlin.test.assertTrue

class IconGroupsTest {

    @Test
    fun getIconGroups_containsFlatMaterialAndHeadphoneOptions() {
        val keys = getIconGroups().flatMap { group -> group.icons }.map { option -> option.key }.toSet()

        assertTrue(keys.contains("icon_flat_material"))
        assertTrue(keys.contains("Headphone"))
    }
}
