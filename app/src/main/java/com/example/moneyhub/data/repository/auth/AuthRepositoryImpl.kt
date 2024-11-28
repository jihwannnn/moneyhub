package com.example.moneyhub.data.repository.auth

import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.Role
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    override suspend fun signUp(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()

            // Update user profile with additional information
            authResult.user?.let { user ->
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                user.updateProfile(profileUpdates).await()
                // user.updatePhoneNumber(phone).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<CurrentUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user

            if (user != null) {
                val currentUser = CurrentUser(
                    id = user.uid,
                    name = user.displayName ?: "",
                    currentGid = "",  // 기본값 사용
                    currentGname = "", // 기본값 사용
                    role = Role.REGULAR // 기본값 사용
                )
                Result.success(currentUser)
            } else {
                Result.failure(Exception("로그인 실패: 사용자 정보를 찾을 수 없습니다"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            auth.currentUser?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<CurrentUser> {

        return try{
            val user = auth.currentUser
            if (user != null) {
                val currentUser = CurrentUser(
                    id = user.uid,
                    name = user.displayName ?: "",
                    currentGid = "",  // 기본값 사용
                    currentGname = "", // 기본값 사용
                    role = Role.REGULAR // 기본값 사용
                )
                Result.success(currentUser)
            } else {
                Result.failure(Exception("자동 로그인 실패: 다시 로그인 해주세요"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}