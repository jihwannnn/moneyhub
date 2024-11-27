package com.example.moneyhub.activity.postonboard

import com.example.moneyhub.model.Post
import kotlinx.coroutines.delay
import javax.inject.Inject

class PostRepository1 @Inject constructor(): PostRepository {

    // Mock function to simulate a network request or database insert
    override suspend fun createPost(post: Post): Result<Unit> {
        return try {
            // Simulate network delay
            delay(1000)
            // Simulate success
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}