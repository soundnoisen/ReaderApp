package com.example.core.domain.model.reader

sealed class BookContent {
    data class Text(val content: List<String>) : BookContent()
    data class Pdf(val path: String, val pageCount: Int, val startPage: Int) : BookContent()
    data class Error(val error: BookContentError) : BookContent()
}

sealed class BookContentError {
    object FileNotFound: BookContentError()
    object InvalidFormat: BookContentError()
    object UnableReadFile: BookContentError()
    object EmptyFile: BookContentError()
}

