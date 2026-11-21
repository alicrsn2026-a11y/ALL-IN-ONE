package com.allinone.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.allinone.app.data.db.entity.EmailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDao {
    @Query("SELECT * FROM emails ORDER BY date DESC")
    fun observeAll(): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE folder = :folder ORDER BY date DESC")
    fun observeFolder(folder: String): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE id = :id")
    suspend fun getById(id: Long): EmailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(email: EmailEntity): Long

    @Update
    suspend fun update(email: EmailEntity)

    @Delete
    suspend fun delete(email: EmailEntity)
}
