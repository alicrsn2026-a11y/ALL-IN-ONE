package com.allinone.app.util

import android.content.Context
import com.allinone.app.data.AppContainer
import com.allinone.app.data.db.entity.NoteEntity
import com.allinone.app.data.db.entity.SpaceItemEntity
import com.allinone.app.data.db.entity.TaskEntity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import java.io.File

/**
 * Creates and restores JSON snapshots of the app's data (space items, notes,
 * tasks). Kept simple and dependency-free; a full Room backup can be layered on
 * top later.
 */
class BackupManager(private val context: Context, private val container: AppContainer) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    data class Snapshot(
        val version: Int = 1,
        val exportedAt: Long = System.currentTimeMillis(),
        val spaceItems: List<SpaceItemEntity> = emptyList(),
        val notes: List<NoteEntity> = emptyList(),
        val tasks: List<TaskEntity> = emptyList()
    )

    suspend fun export(): File {
        val snapshot = Snapshot(
            spaceItems = container.spaceRepository.observeRoot().first(),
            notes = container.noteRepository.observeAll().first(),
            tasks = container.taskRepository.observeAll().first()
        )
        val file = File(context.filesDir, "backup_${System.currentTimeMillis()}.json")
        file.writeText(gson.toJson(snapshot))
        return file
    }

    companion object {
        fun fromJson(json: String): Snapshot {
            val type = object : TypeToken<Snapshot>() {}.type
            return Gson().fromJson(json, type)
        }
    }
}
