package com.allinone.app.data.repository

import com.allinone.app.data.db.dao.SpaceDao
import com.allinone.app.data.db.entity.SpaceItemEntity
import kotlinx.coroutines.flow.Flow

class SpaceRepository(private val dao: SpaceDao) {

    fun observeRoot(): Flow<List<SpaceItemEntity>> = dao.observeRoot()

    fun observeInFolder(folderId: Long): Flow<List<SpaceItemEntity>> = dao.observeInFolder(folderId)

    suspend fun getById(id: Long): SpaceItemEntity? = dao.getById(id)

    fun search(query: String): Flow<List<SpaceItemEntity>> = dao.search(query)

    suspend fun insert(item: SpaceItemEntity): Long = dao.insert(item)

    suspend fun update(item: SpaceItemEntity) = dao.update(item)

    suspend fun delete(item: SpaceItemEntity) = dao.delete(item)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    /** Create a folder inside [parentId] (null = root). */
    suspend fun createFolder(title: String, parentId: Long?, color: Int = 0xFF7C4DFF.toInt()): Long {
        return dao.insert(
            SpaceItemEntity(
                folderId = parentId,
                type = SpaceItemEntity.TYPE_FOLDER,
                title = title,
                color = color,
                icon = "folder"
            )
        )
    }

    /** Add an existing piece of content to the space. */
    suspend fun addContent(
        type: String,
        title: String,
        contentRef: String,
        folderId: Long?,
        color: Int = 0xFF7C4DFF.toInt(),
        icon: String? = null
    ): Long {
        return dao.insert(
            SpaceItemEntity(
                folderId = folderId,
                type = type,
                title = title,
                contentRef = contentRef,
                color = color,
                icon = icon
            )
        )
    }
}
