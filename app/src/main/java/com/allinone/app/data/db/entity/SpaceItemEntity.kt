package com.allinone.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single object inside the Infinite Space. A folder is just a SpaceItem whose
 * type is FOLDER; nesting is expressed through [folderId].
 */
@Entity(
    tableName = "space_items",
    indices = [Index("folderId"), Index("type"), Index("title")]
)
data class SpaceItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val folderId: Long? = null,
    val type: String = "DOCUMENT",
    val title: String = "",
    val subtitle: String? = null,
    /** Reference to the concrete content (note id, email id, uri, etc.). */
    val contentRef: String? = null,
    /** Position in the space. */
    val x: Float = 0f,
    val y: Float = 0f,
    /** Visual scale (size) of the object. */
    val scale: Float = 1f,
    /** Depth layer (0 = front, larger = further back). */
    val depth: Float = 0f,
    val color: Int = 0xFF7C4DFF.toInt(),
    val icon: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val TYPE_FOLDER = "FOLDER"
        const val TYPE_DOCUMENT = "DOCUMENT"
        const val TYPE_NOTE = "NOTE"
        const val TYPE_TASK = "TASK"
        const val TYPE_EMAIL = "EMAIL"
        const val TYPE_CONVERSATION = "CONVERSATION"
        const val TYPE_RESEARCH = "RESEARCH"
        const val TYPE_SCAN = "SCAN"
        const val TYPE_VIDEO = "VIDEO"
        const val TYPE_IMAGE = "IMAGE"
        const val TYPE_PDF = "PDF"
        const val TYPE_SHORTCUT = "SHORTCUT"
    }
}
