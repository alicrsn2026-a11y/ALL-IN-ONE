package com.allinone.app.ui.scanner

import android.graphics.BitmapFactory
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allinone.app.data.AppContainer
import com.allinone.app.data.db.entity.ScanEntity
import com.allinone.app.util.PdfExporter
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executor

@Composable
fun ScannerScreen(container: AppContainer) {
    val context = LocalContext.current
    val repo = container.scanRepository
    val scans by repo.observeAll().collectAsStateWithLifecycle(
        initialValue = emptyList(),
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )
    val scope = rememberCoroutineScope()
    var capturedText by remember { mutableStateOf("") }
    var lastImageFile by remember { mutableStateOf<File?>(null) }

    val executor: Executor = remember { ContextCompat.getMainExecutor(context) }
    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val capture = ImageCapture.Builder().build()
        imageCapture = capture
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as androidx.lifecycle.LifecycleOwner,
                androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                capture
            )
        } catch (e: Exception) {
            // camera unavailable
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(12.dp)) {
            Text("Scanner", style = MaterialTheme.typography.titleLarge)
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (capturedText.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Text OCR detectat:", style = MaterialTheme.typography.titleSmall)
                        Text(capturedText, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(scans, key = { it.id }) { s ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(s.title, style = MaterialTheme.typography.titleSmall)
                                Text(s.ocrText.take(80), style = MaterialTheme.typography.bodySmall)
                            }
                            if (s.pdfPath != null) {
                                IconButton(onClick = { /* open pdf */ }) {
                                    Icon(Icons.Filled.PictureAsPdf, contentDescription = "PDF")
                                }
                            }
                            IconButton(onClick = { scope.launch { repo.delete(s) } }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Șterge")
                            }
                        }
                    }
                }
            }
        }
        IconButton(
            onClick = {
                val capture = imageCapture
                if (capture != null) {
                    val file = File(context.cacheDir, "scan_${System.currentTimeMillis()}.jpg")
                    val options = ImageCapture.OutputFileOptions.Builder(file).build()
                    capture.takePicture(
                        options,
                        executor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                scope.launch {
                                    val bitmap = withContext(Dispatchers.IO) {
                                        BitmapFactory.decodeFile(file.absolutePath)
                                    }
                                    val text = if (bitmap != null) {
                                        val image = InputImage.fromBitmap(bitmap, 0)
                                        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                                        val result = withContext(Dispatchers.IO) {
                                            Tasks.await(recognizer.process(image))
                                        }
                                        result.text
                                    } else ""
                                    val pdf = bitmap?.let { PdfExporter.exportBitmapToPdf(context, it, "scan_${System.currentTimeMillis()}") }
                                    repo.insert(
                                        ScanEntity(
                                            title = "Scanare ${System.currentTimeMillis()}",
                                            imagePath = file.absolutePath,
                                            pdfPath = pdf?.absolutePath,
                                            ocrText = text
                                        )
                                    )
                                    capturedText = text
                                    lastImageFile = file
                                }
                            }

                            override fun onError(exception: ImageCaptureException) {
                                capturedText = "Eroare captură: ${exception.message}"
                            }
                        }
                    )
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) {
            Icon(Icons.Filled.CameraAlt, contentDescription = "Capturează")
        }
    }
}
