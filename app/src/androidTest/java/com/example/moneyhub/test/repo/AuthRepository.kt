package com.example.moneyhub.test.repo

import com.example.moneyhub.model.CurrentUser

interface AuthRepository {

    // 로그인
    suspend fun signIn(email: String, password: String): Result<CurrentUser>

    // 회원가입
    suspend fun signUp(name: String, email: String, phone: String, password: String): Result<String>

    // 로그아웃
    suspend fun signOut(): Result<Unit>

    // 계정 삭제
    suspend fun deleteAccount(): Result<Unit>

    // 현재 유저 정보 가져오기
    suspend fun getCurrentUser(): Result<CurrentUser>
}
