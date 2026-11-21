package com.allinone.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "research_results")
data class ResearchResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val query: String = "",
    val title: String = "",
    val snippet: String = "",
    val url: String? = null,
    val source: String = "",
    val savedAt: Long = System.currentTimeMillis()
)
