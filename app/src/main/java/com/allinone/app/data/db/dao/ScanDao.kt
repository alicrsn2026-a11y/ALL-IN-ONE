package com.allinone.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.allinone.app.data.db.entity.ScanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Query("SELECT * FROM scans ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ScanEntity>>

    @Query("SELECT * FROM scans WHERE id = :id")
    suspend fun getById(id: Long): ScanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scan: ScanEntity): Long

    @Delete
    suspend fun delete(scan: ScanEntity)
}
