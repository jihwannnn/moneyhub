package com.example.moneyhub.test.repo
import com.google.firebase.auth.FirebaseAuth

import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.Role
import com.google.firebase.auth.userProfileChangeRequest

import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    override suspend fun signUp(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()

            val user = authResult.user ?: throw Exception("User creation failed")

            user.let { u ->
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                u.updateProfile(profileUpdates).await()
            }


            Result.success(user.uid)
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
                Result.failure(Exception())
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}