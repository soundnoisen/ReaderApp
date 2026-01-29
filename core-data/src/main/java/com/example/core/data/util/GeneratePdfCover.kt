package com.example.core.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap
import java.io.File
import java.util.UUID

fun generatePdfCover(context: Context, pdfFile: File): String? {
    return try {
        if (pdfFile.extension.lowercase() != "pdf") return null

        PdfRenderer(
            ParcelFileDescriptor.open(
                pdfFile,
                ParcelFileDescriptor.MODE_READ_ONLY
            )
        ).use { renderer ->
            if (renderer.pageCount == 0) return null

            renderer.openPage(0).use { page ->
                val bitmap = createBitmap(page.width, page.height)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                val coverDir = File(context.filesDir, "covers").apply { if (!exists()) mkdirs() }
                val coverFile = File(coverDir, "${UUID.randomUUID()}_cover.png")

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, coverFile.outputStream())
                coverFile.absolutePath
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
