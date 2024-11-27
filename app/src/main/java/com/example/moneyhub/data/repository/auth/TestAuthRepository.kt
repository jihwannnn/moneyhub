package com.example.moneyhub.data.repository.auth

import com.example.moneyhub.model.CurrentUser
import javax.inject.Inject

// data/repository/TestAuthRepository.kt
// sign up repo for test
class TestAuthRepository @Inject constructor() : AuthRepository {
    override suspend fun signUp(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<Unit> {
        return Result.success(Unit)
    }
    override suspend fun signIn(
        email: String,
        password: String
    ): Result<CurrentUser> {
        return Result.success(CurrentUser())
    }
    override suspend fun signOut(): Result<Unit> {
        return Result.success(Unit)
    }
    override suspend fun deleteAccount(): Result<Unit> {
        return Result.success(Unit)
    }
    override suspend fun getCurrentUser(): String? { // uid 반환
        return ""
    }
}