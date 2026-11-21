package com.allinone.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scans")
data class ScanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String = "Scanare",
    val imagePath: String? = null,
    val pdfPath: String? = null,
    val ocrText: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
