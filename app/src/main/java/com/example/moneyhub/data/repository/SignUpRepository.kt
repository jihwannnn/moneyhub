package com.example.moneyhub.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

// data/repository/SignUpRepository.kt
// sign up repo
interface SignUpRepository {
    suspend fun signUp(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<Unit>

}
