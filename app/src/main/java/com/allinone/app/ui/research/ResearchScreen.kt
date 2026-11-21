package com.allinone.app.ui.research

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allinone.app.data.AppContainer
import com.allinone.app.data.db.entity.ResearchResultEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ResearchScreen(container: AppContainer) {
    val repo = container.researchRepository
    val saved by repo.observeAll().collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<ResearchResultEntity>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Cercetare AI", style = MaterialTheme.typography.titleLarge)

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Caută un subiect…") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            TextButton(
                onClick = {
                    val q = query.trim()
                    if (q.isNotEmpty()) {
                        scope.launch {
                            searching = true
                            delay(600) // simulate search latency
                            results = listOf(
                                ResearchResultEntity(query = q, title = "Rezultat 1: $q", snippet = "Sinteză generată despre $q. (Mod demo — conectează un furnizor AI în Setări.)", source = "AI"),
                                ResearchResultEntity(query = q, title = "Rezultat 2: $q", snippet = "Documentație și surse asociate cu $q.", source = "Web"),
                                ResearchResultEntity(query = q, title = "Rezultat 3: $q", snippet = "Analiză și concluzii pentru $q.", source = "AI")
                            )
                            searching = false
                        }
                    }
                }
            ) { Text(if (searching) "…" else "Caută") }
        }

        if (results.isNotEmpty()) {
            Text("Rezultate", style = MaterialTheme.typography.titleMedium)
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(results, key = { it.id }) { r ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(r.title, style = MaterialTheme.typography.titleMedium)
                            Text(r.snippet, style = MaterialTheme.typography.bodyMedium)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                IconButton(onClick = {
                                    scope.launch { repo.insert(r) }
                                }) {
                                    Icon(Icons.Filled.BookmarkAdd, contentDescription = "Salvează")
                                }
                            }
                        }
                    }
                }
            }
        }

        Text("Salvate", style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(saved, key = { it.id }) { r ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(12.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text(r.title, style = MaterialTheme.typography.titleMedium)
                            Text(r.snippet, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { scope.launch { repo.delete(r) } }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Șterge")
                        }
                    }
                }
            }
        }
    }
}
