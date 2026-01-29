package com.example.core.domain.model.auth

import com.example.core.domain.model.User

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val error: AuthError) : AuthResult()
}