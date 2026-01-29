package com.example.core.data.mapper

import com.example.core.domain.model.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toDomain() = User(
    uid = uid,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl
)