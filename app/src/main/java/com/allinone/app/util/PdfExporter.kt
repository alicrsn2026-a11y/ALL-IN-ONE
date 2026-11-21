package com.allinone.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

/**
 * Creates a single-page PDF from a bitmap and stores it in the app's external
 * documents directory so it can be shared / picked later.
 */
object PdfExporter {

    fun exportBitmapToPdf(context: Context, bitmap: Bitmap, title: String): File? {
        return try {
            val dir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                "scans"
            )
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "$title.pdf")

            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(
                bitmap.width,
                bitmap.height,
                1
            ).create()
            val page = document.startPage(pageInfo)
            page.canvas.drawBitmap(bitmap, 0f, 0f, null)
            document.finishPage(page)

            FileOutputStream(file).use { document.writeTo(it) }
            document.close()
            file
        } catch (e: Exception) {
            null
        }
    }
}
