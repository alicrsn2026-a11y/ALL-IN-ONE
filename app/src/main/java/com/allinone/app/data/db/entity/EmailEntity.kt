package com.allinone.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emails")
data class EmailEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val subject: String = "",
    val fromAddress: String = "",
    val fromName: String = "",
    val toAddress: String = "",
    val body: String = "",
    val date: Long = System.currentTimeMillis(),
    val read: Boolean = false,
    val folder: String = "INBOX",
    /** JSON-encoded list of attachment descriptors. */
    val attachments: String = "[]"
) {
    companion object {
        const val FOLDER_INBOX = "INBOX"
        const val FOLDER_SENT = "SENT"
        const val FOLDER_DRAFTS = "DRAFTS"
        const val FOLDER_ARCHIVE = "ARCHIVE"
    }
}
