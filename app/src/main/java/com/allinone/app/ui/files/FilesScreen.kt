package com.allinone.app.ui.files

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.allinone.app.data.AppContainer

@Composable
fun FilesScreen(container: AppContainer) {
    val context = LocalContext.current
    var rootUri by remember { mutableStateOf<Uri?>(null) }
    var currentDir by remember { mutableStateOf<DocumentFile?>(null) }
    var children by remember { mutableStateOf<List<DocumentFile>>(emptyList()) }
    var query by remember { mutableStateOf("") }

    val openTreeLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            rootUri = uri
            val dir = DocumentFile.fromTreeUri(context, uri)
            currentDir = dir
            children = dir?.listFiles()?.toList() ?: emptyList()
        }
    }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Fișiere", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            TextButton(onClick = { openTreeLauncher.launch(null) }) {
                Icon(Icons.Filled.FolderOpen, contentDescription = null)
                Text(" Deschide folder")
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Caută fișiere…") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        val filtered = children.filter {
            query.isBlank() || it.name?.contains(query, ignoreCase = true) == true
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(filtered, key = { it.uri.toString() }) { file ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(file.name ?: "(fără nume)", style = MaterialTheme.typography.titleMedium)
                            Text(if (file.isDirectory) "Folder" else "${file.length()} bytes", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = file.type ?: "*/*"
                                putExtra(Intent.EXTRA_STREAM, file.uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, "Partajează"))
                        }) {
                            Icon(Icons.Filled.Share, contentDescription = "Partajează")
                        }
                        IconButton(onClick = {
                            file.delete()
                            children = currentDir?.listFiles()?.toList() ?: emptyList()
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Șterge")
                        }
                    }
                }
            }
        }
    }
}
