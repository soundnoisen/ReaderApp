package com.example.core.domain.model.auth

sealed class AuthError {
    object InvalidCredentials : AuthError()
    object Network : AuthError()
    object Unknown : AuthError()
    object CredentialsNotFound: AuthError()
    object NotValidEmail: AuthError()
    object NotValidPassword: AuthError()
    object DifferentPasswords: AuthError()
    object UserAlreadyExist : AuthError()
}
