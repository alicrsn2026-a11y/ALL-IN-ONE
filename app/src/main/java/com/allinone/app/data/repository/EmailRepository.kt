package com.allinone.app.data.repository

import com.allinone.app.data.db.dao.EmailDao
import com.allinone.app.data.db.entity.EmailEntity
import kotlinx.coroutines.flow.Flow

class EmailRepository(private val dao: EmailDao) {

    fun observeAll(): Flow<List<EmailEntity>> = dao.observeAll()

    fun observeFolder(folder: String): Flow<List<EmailEntity>> = dao.observeFolder(folder)

    suspend fun getById(id: Long): EmailEntity? = dao.getById(id)

    suspend fun insert(email: EmailEntity): Long = dao.insert(email)

    suspend fun update(email: EmailEntity) = dao.update(email)

    suspend fun delete(email: EmailEntity) = dao.delete(email)

    suspend fun markRead(email: EmailEntity, read: Boolean = true) {
        dao.update(email.copy(read = read))
    }

    suspend fun moveToFolder(email: EmailEntity, folder: String) {
        dao.update(email.copy(folder = folder))
    }
}
