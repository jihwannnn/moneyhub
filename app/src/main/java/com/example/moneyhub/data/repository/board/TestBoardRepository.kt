package com.example.moneyhub.data.repository.board

import com.example.moneyhub.model.Comment
import com.example.moneyhub.model.Post
import javax.inject.Inject

class TestBoardRepository @Inject constructor() : BoardRepository {
    override suspend fun createPost(post: Post): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun updatePost(post: Post): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun deletePost(
        gid: String,
        pid: String
    ): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getPosts(gid: String): Result<List<Post>> {
        return Result.success(emptyList())
    }

    override suspend fun addComment(comment: Comment): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun updateComment(comment: Comment): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun deleteComment(
        gid: String,
        pid: String,
        cid: String
    ): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getComments(
        gid: String,
        pid: String
    ): Result<List<Comment>> {
        return Result.success(emptyList())
    }
}