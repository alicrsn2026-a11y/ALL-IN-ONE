package com.allinone.app.data.repository

import com.allinone.app.data.db.dao.NoteDao
import com.allinone.app.data.db.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {

    fun observeAll(): Flow<List<NoteEntity>> = dao.observeAll()

    suspend fun getById(id: Long): NoteEntity? = dao.getById(id)

    suspend fun insert(note: NoteEntity): Long = dao.insert(note)

    suspend fun update(note: NoteEntity) = dao.update(note)

    suspend fun delete(note: NoteEntity) = dao.delete(note)
}
