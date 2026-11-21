package com.allinone.app.ui.space

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.abs
import androidx.lifecycle.viewmodel.compose.viewModel
import com.allinone.app.data.AppContainer
import com.allinone.app.data.db.entity.SpaceItemEntity
import com.allinone.app.util.containerFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.abs

@Composable
fun InfiniteSpaceScreen(container: AppContainer) {
    val vm: SpaceViewModel = viewModel(factory = containerFactory(container) { SpaceViewModel(it) })
    val items by vm.items.collectAsStateWithLifecycle(
        initialValue = emptyList(),
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )
    val folderPath by vm.folderPath.collectAsStateWithLifecycle(
        initialValue = emptyList(),
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )

    var zoom by remember { mutableFloatStateOf(1f) }
    var camX by remember { mutableFloatStateOf(0f) }
    var camY by remember { mutableFloatStateOf(0f) }
    var selectedId by remember { mutableStateOf<Long?>(null) }
    var menuItem by remember { mutableStateOf<SpaceItemEntity?>(null) }
    var show2d by remember { mutableStateOf(false) }
    var searching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var detailItem by remember { mutableStateOf<SpaceItemEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val searchResults by remember(query) { vm.search(query) }.collectAsStateWithLifecycle(
        initialValue = emptyList(),
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )

    var canvasW by remember { mutableFloatStateOf(0f) }
    var canvasH by remember { mutableFloatStateOf(0f) }

    Box(
        Modifier
            .fillMaxSize()
            .onSizeChanged { canvasW = it.width.toFloat(); canvasH = it.height.toFloat() }
    ) {
        fun screenToWorldX(sx: Float) = (sx - canvasW / 2f) / zoom + camX
        fun screenToWorldY(sy: Float) = (sy - canvasH / 2f) / zoom + camY
        fun worldToScreenX(wx: Float) = (wx - camX) * zoom + canvasW / 2f
        fun worldToScreenY(wy: Float) = (wy - camY) * zoom + canvasH / 2f

        fun hitTest(sx: Float, sy: Float): SpaceItemEntity? {
            return items.lastOrNull { item ->
                val itemSize = 120.dp.value * item.scale * zoom
                val ix = worldToScreenX(item.x)
                val iy = worldToScreenY(item.y)
                abs(sx - ix) <= itemSize / 2f && abs(sy - iy) <= itemSize / 2f
            }
        }

        if (show2d) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(140.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    Card(
                        onClick = {
                            if (item.type == SpaceItemEntity.TYPE_FOLDER) vm.openFolder(item.id)
                            else detailItem = item
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(item.color))
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(item.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                            item.subtitle?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f)) }
                        }
                    }
                }
            }
        } else {
            val textMeasurer = rememberTextMeasurer()
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF12101E))
                    .pointerInput(items) {
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            val sx = down.position.x
                            val sy = down.position.y
                            val hit = hitTest(sx, sy)

                            if (hit != null) {
                                val longPress = withTimeoutOrNull(400) { waitForUpOrCancellation() } == null
                                if (longPress) {
                                    menuItem = hit
                                } else {
                                    var dragging = false
                                    var last = down.position
                                    var ended = false
                                    while (!ended) {
                                        val event = awaitPointerEvent()
                                        val change = event.changes.firstOrNull() ?: break
                                        if (!change.pressed) { ended = true; break }
                                        val delta = change.position - last
                                        if (!dragging && (abs(delta.x) > 6f || abs(delta.y) > 6f)) dragging = true
                                        if (dragging) {
                                            vm.updatePosition(
                                                hit.copy(
                                                    x = hit.x + delta.x / zoom,
                                                    y = hit.y + delta.y / zoom
                                                )
                                            )
                                        }
                                        last = change.position
                                        change.consume()
                                    }
                                    if (!dragging) {
                                        if (hit.type == SpaceItemEntity.TYPE_FOLDER) {
                                            vm.openFolder(hit.id)
                                        } else if (selectedId == hit.id) {
                                            detailItem = hit
                                        } else {
                                            selectedId = hit.id
                                        }
                                    }
                                }
                            } else {
                                // Pan + pinch on empty space
                                var lastCentroid = down.position
                                var lastDist = 0f
                                var accZoom = 1f
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val changes = event.changes
                                    if (changes.none { it.pressed }) break
                                    if (changes.size >= 2) {
                                        val p1 = changes[0].position
                                        val p2 = changes[1].position
                                        val dist = (p1 - p2).getDistance()
                                        if (lastDist > 0f) accZoom *= dist / lastDist
                                        lastDist = dist
                                        lastCentroid = (p1 + p2) / 2f
                                    } else {
                                        val c = changes.first()
                                        val delta = c.position - lastCentroid
                                        camX -= delta.x / zoom
                                        camY -= delta.y / zoom
                                        lastCentroid = c.position
                                    }
                                    changes.forEach { it.consume() }
                                }
                                zoom = (zoom * accZoom).coerceIn(0.2f, 6f)
                            }
                        }
                    }
            ) {
                drawRect(Color(0xFF12101E))
                // starfield
                val starCount = 80
                for (i in 0 until starCount) {
                    val sx = ((i * 137.508f) % size.width)
                    val sy = ((i * 73.7f) % size.height)
                    drawCircle(
                        color = Color.White.copy(alpha = 0.15f + (i % 5) * 0.03f),
                        radius = 1.2f,
                        center = Offset(sx, sy)
                    )
                }
                items.forEach { item ->
                    val itemSize = 120.dp.value * item.scale * zoom
                    val ix = worldToScreenX(item.x)
                    val iy = worldToScreenY(item.y)
                    if (ix > -itemSize && ix < size.width + itemSize &&
                        iy > -itemSize && iy < size.height + itemSize
                    ) {
                        val alpha = (1f - item.depth * 0.15f).coerceIn(0.5f, 1f)
                        val color = Color(item.color).copy(alpha = alpha)
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(ix - itemSize / 2f, iy - itemSize / 2f),
                            size = Size(itemSize, itemSize),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.value)
                        )
                        if (selectedId == item.id) {
                            drawRoundRect(
                                color = Color.White,
                                topLeft = Offset(ix - itemSize / 2f - 3f, iy - itemSize / 2f - 3f),
                                size = Size(itemSize + 6f, itemSize + 6f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.value),
                                style = Stroke(width = 3f)
                            )
                        }
                        drawText(
                            textMeasurer = textMeasurer,
                            text = item.title.take(14),
                            topLeft = Offset(ix - itemSize / 2f + 8.dp.value, iy + itemSize / 2f - 26.dp.value),
                            style = androidx.compose.ui.text.TextStyle(
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        )
                        if (item.type == SpaceItemEntity.TYPE_FOLDER) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.85f),
                                radius = 10.dp.value,
                                center = Offset(ix, iy - 8.dp.value)
                            )
                        }
                    }
                }
            }
        }

        // Top overlay: breadcrumb + search toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (folderPath.isNotEmpty()) {
                IconButton(onClick = { vm.navigateBack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Înapoi", tint = Color.White)
                }
            } else {
                IconButton(onClick = { vm.navigateToRoot() }) {
                    Icon(Icons.Filled.Home, contentDescription = "Acasă", tint = Color.White)
                }
            }
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Spațiu", color = Color.White, style = MaterialTheme.typography.titleMedium)
                folderPath.forEach { f ->
                    Text(" / ${f.title}", color = Color.White.copy(alpha = 0.7f))
                }
            }
            IconButton(onClick = { show2d = !show2d }) {
                Icon(Icons.Filled.GridView, contentDescription = "Mod 2D", tint = Color.White)
            }
            IconButton(onClick = { searching = !searching; query = "" }) {
                Icon(Icons.Filled.Search, contentDescription = "Căutare", tint = Color.White)
            }
        }

        if (searching) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Caută în tot spațiul…") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                searchResults.take(15).forEach { item ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(10.dp)
                                .background(Color(item.color), RoundedCornerShape(3.dp))
                        )
                        Text(
                            "  ${item.title}",
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = {
                            if (item.type == SpaceItemEntity.TYPE_FOLDER) vm.openFolder(item.id)
                            else detailItem = item
                            searching = false
                        }) { Text("Deschide") }
                    }
                }
            }
        }

        // Floating action button to add content
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Adaugă")
        }
    }

    // Long-press context menu
    menuItem?.let { item ->
        Box(Modifier.fillMaxSize()) {
            DropdownMenu(
                expanded = true,
                onDismissRequest = { menuItem = null },
                modifier = Modifier.align(Alignment.Center)
            ) {
                DropdownMenuItem(text = { Text("Deschide") }, onClick = {
                    if (item.type == SpaceItemEntity.TYPE_FOLDER) vm.openFolder(item.id)
                    else detailItem = item
                    menuItem = null
                })
                DropdownMenuItem(text = { Text("Redenumește") }, onClick = {
                    scope.launch { vm.renameItem(item, item.title + " (editat)") }
                    menuItem = null
                })
                DropdownMenuItem(text = { Text("Șterge") }, onClick = {
                    scope.launch { vm.deleteItem(item) }
                    menuItem = null
                })
                DropdownMenuItem(text = { Text("Anulează") }, onClick = { menuItem = null })
            }
        }
    }

    // Detail / preview dialog
    detailItem?.let { item ->
        AlertDialog(
            onDismissRequest = { detailItem = null },
            title = { Text(item.title) },
            text = {
                Column {
                    Text("Tip: ${item.type}", style = MaterialTheme.typography.bodyMedium)
                    item.subtitle?.let { Text("Detalii: $it") }
                    item.contentRef?.let { Text("Referință: $it") }
                }
            },
            confirmButton = {
                TextButton(onClick = { detailItem = null }) { Text("Închide") }
            }
        )
    }

    // Add dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Adaugă în spațiu") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newFolderName,
                        onValueChange = { newFolderName = it },
                        label = { Text("Nume folder") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newFolderName.isNotBlank()) {
                        scope.launch { vm.createFolder(newFolderName) }
                    }
                    newFolderName = ""
                    showAddDialog = false
                }) { Text("Creează folder") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Anulează") }
            }
        )
    }
}
