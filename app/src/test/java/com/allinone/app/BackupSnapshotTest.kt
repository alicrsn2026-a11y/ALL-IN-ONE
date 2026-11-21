package com.allinone.app

import com.allinone.app.data.db.entity.SpaceItemEntity
import com.allinone.app.util.BackupManager
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupSnapshotTest {

    @Test
    fun `space item json round trip preserves fields`() {
        val item = SpaceItemEntity(
            id = 7L,
            folderId = 3L,
            type = SpaceItemEntity.TYPE_FOLDER,
            title = "Proiecte",
            x = 120.5f,
            y = -40f,
            scale = 1.5f,
            depth = 2f,
            color = 0xFF112233.toInt()
        )
        val json = Gson().toJson(item)
        val restored = Gson().fromJson(json, SpaceItemEntity::class.java)
        assertEquals(item.id, restored.id)
        assertEquals(item.folderId, restored.folderId)
        assertEquals(item.type, restored.type)
        assertEquals(item.title, restored.title)
        assertEquals(item.x, restored.x)
        assertEquals(item.y, restored.y)
        assertEquals(item.scale, restored.scale)
        assertEquals(item.depth, restored.depth)
        assertEquals(item.color, restored.color)
    }

    @Test
    fun `snapshot json round trip`() {
        val snapshot = BackupManager.Snapshot(
            spaceItems = listOf(
                SpaceItemEntity(title = "A", type = SpaceItemEntity.TYPE_DOCUMENT)
            )
        )
        val json = Gson().toJson(snapshot)
        val restored = BackupManager.fromJson(json)
        assertEquals(1, restored.spaceItems.size)
        assertEquals("A", restored.spaceItems[0].title)
        assertTrue(restored.version == 1)
    }
}
