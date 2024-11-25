package com.example.moneyhub.data.repository

import javax.inject.Inject

// data/repository/TestSignUpRepository.kt
// sign up repo for test
class TestSignUpRepository @Inject constructor() : SignUpRepository {
    override suspend fun signUp(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<Unit> {
        return Result.success(Unit)
    }
}