package com.example.core.domain.model.book

sealed class DeleteResult {
    object Success : DeleteResult()
    data class Error(val error: DeleteError) : DeleteResult()
}

sealed class DeleteError {
    object Network: DeleteError()
    data class Unknown(val error: String): DeleteError()
}