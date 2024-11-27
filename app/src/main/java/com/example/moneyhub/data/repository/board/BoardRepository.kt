package com.example.moneyhub.data.repository.board

import com.example.moneyhub.model.Comment
import com.example.moneyhub.model.Post

interface BoardRepository {
    suspend fun createPost(gid: String, post: Post): Result<String> // pid 반환
    suspend fun updatePost(gid: String, pid: String, title: String, content: String): Result<Unit>
    suspend fun deletePost(gid: String, pid: String): Result<Unit>
    suspend fun getPosts(gid: String): Result<List<Post>>
    suspend fun addComment(gid: String, pid: String, comment: Comment): Result<String> // cid 반환
    suspend fun updateComment(gid: String, pid: String, cid: String, content: String): Result<Unit>
    suspend fun deleteComment(gid: String, pid: String, cid: String): Result<Unit>
    suspend fun getComments(gid: String, pid: String): Result<List<Comment>>
}