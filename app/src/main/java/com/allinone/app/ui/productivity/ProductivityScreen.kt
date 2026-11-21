package com.allinone.app.ui.productivity

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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allinone.app.data.AppContainer
import com.allinone.app.data.db.entity.NoteEntity
import com.allinone.app.data.db.entity.TaskEntity
import kotlinx.coroutines.launch

@Composable
fun ProductivityScreen(container: AppContainer) {
    val noteRepo = container.noteRepository
    val taskRepo = container.taskRepository
    val notes by noteRepo.observeAll().collectAsStateWithLifecycle(
        initialValue = emptyList(),
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )
    val tasks by taskRepo.observeAll().collectAsStateWithLifecycle(
        initialValue = emptyList(),
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var tab by remember { mutableIntStateOf(0) }
    var editingNote by remember { mutableStateOf<NoteEntity?>(null) }
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var newTaskTitle by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Productivitate", style = MaterialTheme.typography.titleLarge)

        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Note") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Sarcini") })
        }

        when (tab) {
            0 -> {
                if (editingNote != null) {
                    Column(Modifier.fillMaxSize()) {
                        OutlinedTextField(value = noteTitle, onValueChange = { noteTitle = it }, label = { Text("Titlu") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = noteContent, onValueChange = { noteContent = it }, label = { Text("Conținut") }, modifier = Modifier.fillMaxWidth().weight(1f))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = {
                                val n = editingNote!!
                                scope.launch {
                                    noteRepo.update(n.copy(title = noteTitle, content = noteContent, updatedAt = System.currentTimeMillis()))
                                }
                                editingNote = null
                            }) { Text("Salvează") }
                            TextButton(onClick = { editingNote = null }) { Text("Anulează") }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notes, key = { it.id }) { n ->
                            Card(onClick = {
                                editingNote = n
                                noteTitle = n.title
                                noteContent = n.content
                            }, modifier = Modifier.fillMaxWidth()) {
                                Row(Modifier.fillMaxWidth().padding(12.dp)) {
                                    Column(Modifier.weight(1f)) {
                                        Text(n.title, style = MaterialTheme.typography.titleMedium)
                                        Text(n.content.take(80), style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { scope.launch { noteRepo.delete(n) } }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Șterge")
                                    }
                                }
                            }
                        }
                    }
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                val id = noteRepo.insert(NoteEntity(title = "Notă nouă"))
                                editingNote = noteRepo.getById(id)
                                noteTitle = "Notă nouă"
                                noteContent = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Notă nouă")
                    }
                }
            }
            1 -> {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        placeholder = { Text("Sarcină nouă…") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    TextButton(onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            scope.launch { taskRepo.insert(TaskEntity(title = newTaskTitle)) }
                            newTaskTitle = ""
                        }
                    }) { Text("Adaugă") }
                }
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(tasks, key = { it.id }) { t ->
                        Card(onClick = { scope.launch { taskRepo.toggle(t) } }, modifier = Modifier.fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    if (t.done) "☑" else "☐",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Column(Modifier.weight(1f).padding(start = 8.dp)) {
                                    Text(
                                        t.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (t.done) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                    t.dueDate?.let {
                                        Text("Termen: $it", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                IconButton(onClick = {
                                    scope.launch { taskRepo.delete(t) }
                                }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Șterge")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
