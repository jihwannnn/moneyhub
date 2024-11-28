package com.example.moneyhub.data.repository.board

import com.example.moneyhub.model.Comment
import com.example.moneyhub.model.Post

interface BoardRepository {
    suspend fun createPost(post: Post): Result<Unit>
    suspend fun updatePost(post: Post): Result<Unit>
    suspend fun deletePost(gid: String, pid: String): Result<Unit>
    suspend fun getPosts(gid: String): Result<List<Post>>
    suspend fun addComment(comment: Comment): Result<Unit>
    suspend fun updateComment(comment: Comment): Result<Unit>
    suspend fun deleteComment(gid: String, pid: String, cid: String): Result<Unit>
    suspend fun getComments(gid: String, pid: String): Result<List<Comment>>
}