package com.example.core.data.util

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.File

class ProgressRequestBody(
    private val file: File,
    private val contentType: String = "application/octet-stream",
    private val onProgress: (percent: Int) -> Unit
) : RequestBody() {

    override fun contentType() = contentType.toMediaTypeOrNull()
    override fun contentLength() = file.length()

    override fun writeTo(sink: BufferedSink) {
        val length = contentLength()
        var uploaded = 0L
        val source = file.source()
        var read: Long

        while (source.read(sink.buffer, 8 * 1024).also { read = it } != -1L) {
            uploaded += read
            sink.flush()
            val progress = (100 * uploaded / length).toInt()
            onProgress(progress)
        }
    }
}