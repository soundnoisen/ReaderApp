package com.example.core.domain.model.book

sealed class DownloadProgress {
    object Idle: DownloadProgress()
    data class Downloading(val percent: Int) : DownloadProgress()
    object Success: DownloadProgress()
    data class Error(val error: DownloadError) : DownloadProgress()
}

sealed class DownloadError {
    object Network: DownloadError()
    object FileNotFound: DownloadError()
    object CoverGenerationFailed: DownloadError()
    object Unknown: DownloadError()
}