package com.example.moneyhub.data.repository.auth

import com.example.moneyhub.model.CurrentUser

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<CurrentUser>
    suspend fun signUp(name: String, email: String, phone: String, password: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun getCurrentUser(): Result<CurrentUser> // uid 반환
}
