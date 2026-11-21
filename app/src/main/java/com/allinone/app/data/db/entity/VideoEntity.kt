package com.allinone.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String = "",
    val uri: String = "",
    val durationMs: Long = 0L,
    val thumbnailPath: String? = null,
    val playlist: String = "Implicită",
    val addedAt: Long = System.currentTimeMillis()
)
