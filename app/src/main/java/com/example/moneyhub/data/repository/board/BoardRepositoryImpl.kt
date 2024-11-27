package com.example.moneyhub.data.repository.board

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.moneyhub.model.Comment
import com.example.moneyhub.model.Post

class BoardRepositoryImpl : BoardRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override suspend fun createPost(gid: String, post: Post): Result<String> {
        TODO()
    }

    override suspend fun updatePost(gid: String, pid: String, title: String, content: String): Result<Unit> {
        TODO()
    }

    override suspend fun deletePost(gid: String, pid: String): Result<Unit> {
        TODO()
    }

    override suspend fun getPosts(gid: String): Result<List<Post>> {
        TODO()
    }

    override suspend fun addComment(gid: String, pid: String, comment: Comment): Result<String> {
        TODO()
    }

    override suspend fun updateComment(gid: String, pid: String, cid: String, content: String): Result<Unit> {
        TODO()
    }

    override suspend fun deleteComment(gid: String, pid: String, cid: String): Result<Unit> {
        TODO()
    }

    override suspend fun getComments(gid: String, pid: String): Result<List<Comment>> {
        TODO()
    }
}