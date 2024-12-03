package com.example.moneyhub.activity.postonboard

import com.example.moneyhub.model.Post

interface PostRepository {

    suspend fun createPost(post: Post): Result<Unit>
    suspend fun getPost(postId: String): Post?
}