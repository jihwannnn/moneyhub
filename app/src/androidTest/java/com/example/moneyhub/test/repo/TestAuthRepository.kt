package com.example.moneyhub.test.repo

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
    ): Result<String> {
        return Result.success("")
    }
    override suspend fun signIn(
        email: String,
        password: String
    ): Result<CurrentUser> {
        return Result.success(CurrentUser("me", "나"))
    }
    override suspend fun signOut(): Result<Unit> {
        return Result.success(Unit)
    }
    override suspend fun deleteAccount(): Result<Unit> {
        return Result.success(Unit)
    }
    override suspend fun getCurrentUser(): Result<CurrentUser> {
        val currentUser = CurrentUser()
        return Result.success(currentUser)
    }
}