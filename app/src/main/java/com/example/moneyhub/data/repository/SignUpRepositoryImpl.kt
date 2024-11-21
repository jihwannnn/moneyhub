package com.example.moneyhub.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

// data/repository/SignUpRepositoryImpl.kt
// sign up repoImpl

class SignUpRepositoryImpl : SignUpRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override suspend fun signUp(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<Unit> {
        return try {

            val authResult = auth.createUserWithEmailAndPassword(email, password).await()

            authResult.user?.let { user ->
                val userData = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "phone" to phone,
                    "createdAt" to FieldValue.serverTimestamp()
                )

                db.collection("users")
                    .document(user.uid)
                    .set(userData)
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
