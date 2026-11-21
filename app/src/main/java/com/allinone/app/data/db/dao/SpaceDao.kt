package com.allinone.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.allinone.app.data.db.entity.SpaceItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpaceDao {
    @Query("SELECT * FROM space_items ORDER BY depth ASC, createdAt DESC")
    fun observeAll(): Flow<List<SpaceItemEntity>>

    @Query("SELECT * FROM space_items WHERE folderId IS NULL ORDER BY depth ASC, createdAt DESC")
    fun observeRoot(): Flow<List<SpaceItemEntity>>

    @Query("SELECT * FROM space_items WHERE folderId = :folderId ORDER BY depth ASC, createdAt DESC")
    fun observeInFolder(folderId: Long): Flow<List<SpaceItemEntity>>

    @Query("SELECT * FROM space_items WHERE id = :id")
    suspend fun getById(id: Long): SpaceItemEntity?

    @Query("SELECT * FROM space_items WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%' OR LOWER(COALESCE(subtitle,'')) LIKE '%' || LOWER(:query) || '%'")
    fun search(query: String): Flow<List<SpaceItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SpaceItemEntity): Long

    @Update
    suspend fun update(item: SpaceItemEntity)

    @Delete
    suspend fun delete(item: SpaceItemEntity)

    @Query("DELETE FROM space_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM space_items WHERE folderId = :folderId")
    suspend fun deleteInFolder(folderId: Long)
}
