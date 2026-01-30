package com.example.core.domain.repository

import com.example.core.domain.model.User
import com.example.core.domain.model.auth.AuthResult
import com.example.core.domain.model.profile.UpdateProfileResult
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    suspend fun signIn(email: String, password: String): AuthResult
    suspend fun signInWithGoogle(): AuthResult
    suspend fun register(email: String, password: String): AuthResult
    fun currentUser(): User?
    fun observeUser(): Flow<User?>
    fun userAuthorized(): Boolean
    suspend fun updateProfile(name: String?, photoUrl: String?): UpdateProfileResult
    suspend fun logout()
}