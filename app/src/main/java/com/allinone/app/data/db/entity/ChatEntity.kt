package com.allinone.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String = "Conversație nouă",
    val provider: String = "openai",
    val model: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val conversationId: Long = 0L,
    val role: String = "user", // "user" | "assistant"
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
