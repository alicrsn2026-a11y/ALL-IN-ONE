package com.allinone.app.ui.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allinone.app.data.AppContainer
import com.allinone.app.data.db.entity.MessageEntity
import kotlinx.coroutines.launch

@Composable
fun AiScreen(container: AppContainer) {
    val chatRepo = container.chatRepository
    val aiService = remember { AiService(container.encryptedPrefs) }
    val conversations by chatRepo.observeConversations().collectAsStateWithLifecycle(
        initialValue = emptyList(),
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )

    var activeConversationId by remember { mutableLongStateOf(-1L) }
    val messages by chatRepo.observeMessages(activeConversationId)
        .collectAsStateWithLifecycle(
            initialValue = emptyList(),
            lifecycle = LocalLifecycleOwner.current.lifecycle
        )
    var input by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        if (activeConversationId == -1L) {
            // Conversation list
            Text("Conversații", style = MaterialTheme.typography.titleLarge)
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(conversations, key = { it.id }) { c ->
                    Card(
                        onClick = { activeConversationId = c.id },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(c.title, style = MaterialTheme.typography.titleMedium)
                                Text(c.provider, style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = {
                                scope.launch { chatRepo.deleteConversation(c) }
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Șterge")
                            }
                        }
                    }
                }
            }
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        activeConversationId = chatRepo.createConversation()
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Conversație nouă")
            }
        } else {
            // Chat view
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { activeConversationId = -1L }) { Text("← Conversații") }
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { m ->
                    MessageBubble(m)
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Scrie un mesaj…") },
                    modifier = Modifier.weight(1f),
                    enabled = !busy
                )
                TextButton(
                    onClick = {
                        val text = input.trim()
                        if (text.isNotEmpty() && !busy) {
                            scope.launch {
                                busy = true
                                chatRepo.addMessage(activeConversationId, "user", text)
                                input = ""
                                val history = chatRepo.getMessages(activeConversationId)
                                    .map { it.role to it.content }
                                val reply = aiService.sendMessage("openai", "", history)
                                chatRepo.addMessage(activeConversationId, "assistant", reply)
                                busy = false
                            }
                        }
                    },
                    enabled = !busy
                ) {
                    Text(if (busy) "…" else "Trimite")
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: MessageEntity) {
    val isUser = message.role == "user"
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Text(
                message.content,
                modifier = Modifier.padding(12.dp),
                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
