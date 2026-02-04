package com.example.core.domain.model.profile

import android.net.Uri

sealed class UpdateProfileResult {
    data class Success(val url: String?, val uri: Uri?) : UpdateProfileResult()
    data class Error(val error: UpdateProfileError) : UpdateProfileResult()
}

sealed class UpdateProfileError {
    object UserIsNotAuthorized: UpdateProfileError()
    object InvalidFile: UpdateProfileError()
    object Network: UpdateProfileError()
    object FailedToGetImage: UpdateProfileError()
    object NoChanges: UpdateProfileError()
    data class Unknown(val error: String): UpdateProfileError()
}