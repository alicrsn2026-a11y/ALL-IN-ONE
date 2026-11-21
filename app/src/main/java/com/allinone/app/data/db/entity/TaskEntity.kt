package com.allinone.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String = "",
    val description: String = "",
    val listName: String = "Inbox",
    val dueDate: Long? = null,
    val reminderAt: Long? = null,
    val priority: Int = 0,
    val done: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
