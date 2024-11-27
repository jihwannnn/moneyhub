package com.example.moneyhub.data.repository.board

import com.example.moneyhub.data.model.Comment
import com.example.moneyhub.data.model.Post
import javax.inject.Inject

class TestBoardRepository @Inject constructor() : BoardRepository {
    override suspend fun createPost(gid: String, post: Post): Result<String> {
        return Result.success("")
    }

    override suspend fun updatePost(gid: String, pid: String, title: String, content: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun deletePost(gid: String, pid: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getPosts(gid: String): Result<List<Post>> {
        return Result.success(emptyList())
    }

    override suspend fun addComment(gid: String, pid: String, comment: Comment): Result<String> {
        return Result.success("")
    }

    override suspend fun updateComment(gid: String, pid: String, cid: String, content: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun deleteComment(gid: String, pid: String, cid: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getComments(gid: String, pid: String): Result<List<Comment>> {
        return Result.success(emptyList())
    }
}