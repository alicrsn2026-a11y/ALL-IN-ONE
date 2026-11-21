package com.allinone.app.data.repository

import com.allinone.app.data.db.dao.ScanDao
import com.allinone.app.data.db.entity.ScanEntity
import kotlinx.coroutines.flow.Flow

class ScanRepository(private val dao: ScanDao) {

    fun observeAll(): Flow<List<ScanEntity>> = dao.observeAll()

    suspend fun getById(id: Long): ScanEntity? = dao.getById(id)

    suspend fun insert(scan: ScanEntity): Long = dao.insert(scan)

    suspend fun delete(scan: ScanEntity) = dao.delete(scan)
}
