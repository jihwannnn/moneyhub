package com.example.moneyhub.data.repository

// data/repository/TestSignUpRepository.kt
// sign up repo for test
class TestSignUpRepository : SignUpRepository {
    override suspend fun signUp(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<Unit> {
        return Result.success(Unit)
    }
}