package com.allinone.app.ui.video

import android.net.Uri
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.allinone.app.data.AppContainer
import com.allinone.app.data.db.entity.VideoEntity
import kotlinx.coroutines.launch

@Composable
fun VideoScreen(container: AppContainer) {
    val context = LocalContext.current
    val repo = container.videoRepository
    val videos by repo.observeAll().collectAsStateWithLifecycle(
        initialValue = emptyList(),
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )
    val scope = rememberCoroutineScope()

    var playingUri by remember { mutableStateOf<String?>(null) }

    val player = remember {
        ExoPlayer.Builder(context).build()
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Video", style = MaterialTheme.typography.titleLarge)

        playingUri?.let { uri ->
            val playerView = remember { PlayerView(context).apply { useController = true } }
            LaunchedEffect(uri) {
                player.setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
                player.prepare()
                player.playWhenReady = true
                playerView.player = player
            }
            AndroidView(
                factory = { playerView },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(videos, key = { it.id }) { v ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(v.title, style = MaterialTheme.typography.titleMedium)
                            Text(v.playlist, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { playingUri = v.uri }) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Redă")
                        }
                        IconButton(onClick = { scope.launch { repo.delete(v) } }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Șterge")
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                scope.launch {
                    repo.insert(
                        VideoEntity(
                            title = "Video demo ${System.currentTimeMillis()}",
                            uri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                        )
                    )
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Adaugă video")
        }
    }
}
