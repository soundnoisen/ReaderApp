package com.example.feature.book.reader.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.barteksc.pdfviewer.PDFView
import java.io.File

@Composable
fun PdfViewer(
    path: String,
    modifier: Modifier = Modifier,
    currentPage: Int = 0,
    onPageChange: (Int) -> Unit = {}
) {
    AndroidView(
        factory = { context ->
            PDFView(context, null).apply {
                val file = File(path)
                if (file.exists()) {
                    fromFile(file)
                        .defaultPage(currentPage)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .onPageChange { page, _ -> onPageChange(page) }
                        .enableAnnotationRendering(true)
                        .load()
                }
            }
        },
        modifier = modifier
    )
}