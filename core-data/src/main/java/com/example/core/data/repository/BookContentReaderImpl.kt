package com.example.core.data.repository

import android.content.Context
import android.os.ParcelFileDescriptor
import com.example.core.domain.model.reader.BookContent
import com.example.core.domain.model.reader.BookContentError
import com.example.core.domain.repository.BookContentReaderRepository
import com.shockwave.pdfium.PdfiumCore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.File
import java.util.zip.ZipFile

class BookContentReaderImpl @Inject constructor(
    @ApplicationContext private val context: Context
): BookContentReaderRepository {

    override suspend fun getBookContent(localPath: String): BookContent = withContext(Dispatchers.IO) {
        val file = File(localPath)
        if (!file.exists()) return@withContext BookContent.Error(BookContentError.FileNotFound)

        when (file.extension.lowercase()) {
            "txt" -> readTextBook(file)
            "epub" -> readEpubBook(file)
            "pdf" -> readPdfBook(file)
            else -> BookContent.Error(BookContentError.InvalidFormat)
        }
    }

    private fun readTextBook(file: File): BookContent {
        return try {
            BookContent.Text(
                file.readText(Charsets.UTF_8)
                    .split("\n\n","\r\n\r\n")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
            )
        } catch (_: Exception) {
            try {
                BookContent.Text(file.readText(Charsets.ISO_8859_1)
                    .split("\n\n","\r\n\r\n")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                )
            } catch (e: Exception) {
                BookContent.Error(BookContentError.UnableReadFile)
            }
        }
    }

    private fun readEpubBook(file: File): BookContent {
        return try {
            val allLines = mutableListOf<String>()
            ZipFile(file).use { zip ->
                zip.entries().asSequence()
                    .filter { !it.isDirectory && (it.name.endsWith(".html", true) || it.name.endsWith(".xhtml", true)) }
                    .sortedBy { it.name }
                    .forEach { entry ->
                        zip.getInputStream(entry).bufferedReader(Charsets.UTF_8).use { reader ->
                            val htmlContent = reader.readText()
                            val doc = Jsoup.parse(htmlContent)
                            doc.select("script, style").remove()
                            val textContent = doc.body().html()
                            val lines = convertHtmlToLines(textContent)
                            allLines.addAll(lines)
                        }
                    }
            }

            if (allLines.isEmpty()) {
                BookContent.Error(BookContentError.EmptyFile)
            } else {
                BookContent.Text(allLines)
            }
        } catch (e: Exception) {
            BookContent.Error(BookContentError.UnableReadFile)
        }
    }

    private fun convertHtmlToLines(html: String): List<String> {
        val lines = mutableListOf<String>()
        val processed = html
            .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
            .replace(Regex("</(p|div|h[1-6]|li|blockquote)>", RegexOption.IGNORE_CASE), "\n$0")

        val fragments = processed.split("\n")

        fragments.forEach { fragment ->
            val cleanText = fragment.replace(Regex("<[^>]*>"), "")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .trim()

            if (cleanText.isNotEmpty()) {
                lines.add(cleanText)
            }
        }

        return lines
    }

    private fun readPdfBook(file: File): BookContent {
        return try {
            val pdfiumCore = PdfiumCore(context)
                val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

                val pdfDocument = pdfiumCore.newDocument(fd)
                val pageCount = pdfiumCore.getPageCount(pdfDocument)

                pdfiumCore.closeDocument(pdfDocument)
                fd.close()

                BookContent.Pdf(
                    path = file.absolutePath,
                    pageCount = pageCount,
                    startPage = 0
                )
        } catch (e: Exception) {
            BookContent.Error(BookContentError.UnableReadFile)
        }
    }
}