package com.example.core.data.repository

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import com.example.core.data.mapper.toDomain
import com.example.core.data.source.GoogleSignInManager
import com.example.core.domain.model.User
import com.example.core.domain.model.auth.AuthError
import com.example.core.domain.model.auth.AuthResult
import com.example.core.domain.model.profile.UpdateProfileError
import com.example.core.domain.model.profile.UpdateProfileResult
import com.example.core.domain.repository.FirebaseRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInManager: GoogleSignInManager,
): FirebaseRepository {

    override suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            val user = result.user ?: return AuthResult.Error(AuthError.Unknown)

            return AuthResult.Success(user.toDomain())

        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseException(e))
        }
    }

    override suspend fun signInWithGoogle(): AuthResult {
        return try {
            val idToken = googleSignInManager.getGoogleIdToken()
                ?: return AuthResult.Error(AuthError.CredentialsNotFound)

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user ?: return AuthResult.Error(AuthError.Unknown)

            AuthResult.Success(user.toDomain())
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseException(e))
        }
    }

    override suspend fun register(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val user = result.user ?: return AuthResult.Error(AuthError.Unknown)

            return AuthResult.Success(user.toDomain())

        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseException(e))
        }
    }

    private fun mapFirebaseException(e: Exception): AuthError =
        when (e) {
            is FirebaseAuthInvalidCredentialsException ->
                AuthError.InvalidCredentials

            is FirebaseNetworkException ->
                AuthError.Network

            is FirebaseAuthUserCollisionException ->
                AuthError.UserAlreadyExist

            is GetCredentialException ->
                AuthError.CredentialsNotFound

            else -> AuthError.Unknown
        }

    override fun currentUser(): User? = firebaseAuth.currentUser?.toDomain()

    override fun observeUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toDomain())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override fun requireCurrentUserId(): String =
        firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException("User not logged in")

    override fun userAuthorized(): Boolean = firebaseAuth.currentUser != null

    override suspend fun updateProfile(name: String?, photoUrl: String?): UpdateProfileResult {
        val firebaseUser = firebaseAuth.currentUser
            ?: return UpdateProfileResult.Error(UpdateProfileError.UserIsNotAuthorized)

        val request = userProfileChangeRequest {
            if (!name.isNullOrBlank()) displayName = name
            if (!photoUrl.isNullOrBlank()) photoUri = photoUrl.toUri()
        }

        return try {
            firebaseUser.updateProfile(request).await()
            UpdateProfileResult.Success(photoUrl, photoUrl?.toUri())
        } catch (e: Exception) {
            UpdateProfileResult.Error(UpdateProfileError.Unknow(e.message.toString()))
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
        googleSignInManager.clearCredentials()
    }
}