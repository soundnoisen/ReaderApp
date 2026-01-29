package com.example.core.domain.model.book

sealed class UploadProgress {
    object Idle: UploadProgress()
    data class Uploading(val percent: Int) : UploadProgress()
    data class Success(val url: String) : UploadProgress()
    data class Error(val error: UploadError) : UploadProgress()
}

sealed class UploadError {
    object Network: UploadError()
    object InvalidFile: UploadError()
    object InvalidData: UploadError()
    data class Unknown(val error: String): UploadError()
}
