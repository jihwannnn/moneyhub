package com.example.moneyhub.activity.postonboard

import com.example.moneyhub.data.model.Post

interface PostRepository {

    suspend fun createPost(post: Post): Result<Unit>
}