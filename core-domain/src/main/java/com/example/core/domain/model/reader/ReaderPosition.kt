package com.example.core.domain.model.reader

data class ReaderPosition(
    val anchorIndex: Int = 0,
    val progressInItem: Float = 0f
)

fun ReaderPosition.fullProgress(content: BookContent?): Float {
    return when (content) {
        is BookContent.Text -> {
            val textContent = content.content
            if (textContent.isNotEmpty())
                (anchorIndex + progressInItem).coerceIn(0f, textContent.size.toFloat()) / textContent.size
            else 0f
        }
        is BookContent.Pdf -> {
            val pageCount = content.pageCount.coerceAtLeast(1)
            ((anchorIndex.toFloat() + 1f) / pageCount).coerceIn(0f, 1f)
        }
        else -> 0f
    }
}
