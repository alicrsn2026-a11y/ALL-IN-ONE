package com.allinone.app.ui.email

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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allinone.app.data.AppContainer
import com.allinone.app.data.db.entity.EmailEntity
import kotlinx.coroutines.launch

@Composable
fun EmailScreen(container: AppContainer) {
    val repo = container.emailRepository
    var folder by remember { mutableStateOf(EmailEntity.FOLDER_INBOX) }
    val emails by repo.observeFolder(folder).collectAsStateWithLifecycle(
        initialValue = emptyList(),
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )

    var composing by remember { mutableStateOf(false) }
    var subject by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var selectedEmail by remember { mutableStateOf<EmailEntity?>(null) }
    var folderMenu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Email", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            TextButton(onClick = { folderMenu = true }) {
                Text(folder)
            }
            DropdownMenu(expanded = folderMenu, onDismissRequest = { folderMenu = false }) {
                listOf(
                    EmailEntity.FOLDER_INBOX,
                    EmailEntity.FOLDER_SENT,
                    EmailEntity.FOLDER_DRAFTS,
                    EmailEntity.FOLDER_ARCHIVE
                ).forEach { f ->
                    DropdownMenuItem(text = { Text(f) }, onClick = { folder = f; folderMenu = false })
                }
            }
        }

        when {
            selectedEmail != null -> {
                val e = selectedEmail!!
                Column(Modifier.fillMaxSize()) {
                    Text(e.subject, style = MaterialTheme.typography.titleLarge)
                    Text("De la: ${e.fromAddress}", style = MaterialTheme.typography.bodyMedium)
                    Text("Către: ${e.toAddress}", style = MaterialTheme.typography.bodySmall)
                    Text(e.body, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = {
                            scope.launch { repo.moveToFolder(e, EmailEntity.FOLDER_ARCHIVE) }
                            selectedEmail = null
                        }) { Text("Arhivează") }
                        TextButton(onClick = { selectedEmail = null }) { Text("Înapoi") }
                    }
                }
            }
            composing -> {
                Column(Modifier.fillMaxSize()) {
                    OutlinedTextField(value = to, onValueChange = { to = it }, label = { Text("Către") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subiect") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Mesaj") }, modifier = Modifier.fillMaxWidth().weight(1f))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = {
                            scope.launch {
                                repo.insert(
                                    EmailEntity(
                                        subject = subject,
                                        toAddress = to,
                                        body = body,
                                        folder = EmailEntity.FOLDER_SENT,
                                        fromAddress = "eu@allinone.app"
                                    )
                                )
                            }
                            composing = false; subject = ""; to = ""; body = ""
                        }) { Text("Trimite") }
                        TextButton(onClick = {
                            scope.launch {
                                repo.insert(
                                    EmailEntity(
                                        subject = subject,
                                        toAddress = to,
                                        body = body,
                                        folder = EmailEntity.FOLDER_DRAFTS,
                                        fromAddress = "eu@allinone.app"
                                    )
                                )
                            }
                            composing = false
                        }) { Text("Salvează ciornă") }
                        TextButton(onClick = { composing = false }) { Text("Anulează") }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(emails, key = { it.id }) { e ->
                        Card(
                            onClick = {
                                selectedEmail = e
                                scope.launch { repo.markRead(e) }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (e.read) MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(Modifier.fillMaxWidth().padding(12.dp)) {
                                Column(Modifier.weight(1f)) {
                                    Text(e.subject, style = MaterialTheme.typography.titleMedium)
                                    Text(e.fromAddress, style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { scope.launch { repo.delete(e) } }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Șterge")
                                }
                            }
                        }
                    }
                }
                FloatingActionButton(onClick = { composing = true }, modifier = Modifier.align(Alignment.End)) {
                    Icon(Icons.Filled.Add, contentDescription = "Email nou")
                }
            }
        }
    }
}
