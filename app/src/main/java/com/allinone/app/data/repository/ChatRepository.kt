package com.allinone.app.data.repository

import com.allinone.app.data.db.dao.ChatDao
import com.allinone.app.data.db.entity.ConversationEntity
import com.allinone.app.data.db.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val dao: ChatDao) {

    fun observeConversations(): Flow<List<ConversationEntity>> = dao.observeConversations()

    suspend fun getConversation(id: Long): ConversationEntity? = dao.getConversation(id)

    suspend fun createConversation(title: String = "Conversație nouă", provider: String = "openai"): Long {
        return dao.insertConversation(ConversationEntity(title = title, provider = provider))
    }

    suspend fun updateConversation(c: ConversationEntity) = dao.updateConversation(c)

    suspend fun deleteConversation(c: ConversationEntity) {
        dao.deleteMessagesFor(c.id)
        dao.deleteConversation(c)
    }

    fun observeMessages(conversationId: Long): Flow<List<MessageEntity>> =
        dao.observeMessages(conversationId)

    suspend fun getMessages(conversationId: Long): List<MessageEntity> =
        dao.getMessages(conversationId)

    suspend fun addMessage(conversationId: Long, role: String, content: String) {
        dao.insertMessage(
            MessageEntity(
                conversationId = conversationId,
                role = role,
                content = content
            )
        )
        dao.getConversation(conversationId)?.let {
            dao.updateConversation(it.copy(updatedAt = System.currentTimeMillis()))
        }
    }
}
