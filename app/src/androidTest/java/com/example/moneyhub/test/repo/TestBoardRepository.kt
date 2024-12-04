package com.example.moneyhub.test.repo

import com.example.moneyhub.model.Comment
import com.example.moneyhub.model.Post
import kotlinx.coroutines.delay
import javax.inject.Inject

class TestBoardRepository @Inject constructor() : BoardRepository {

    // Mock 데이터
    private val mockPosts = listOf(
        Post(
            pid = "1",
            gid = "group1",
            title = "테스트 제목 1",
            content = "테스트 내용 1",
            authorId = "user1",
            authorName = "사용자 1",
            imageUrl = "",
            commentCount = 3,
            createdAt = System.currentTimeMillis()
        ),
        Post(
            pid = "2",
            gid = "group2",
            title = "테스트 제목 2",
            content = "테스트 내용 2",
            authorId = "user2",
            authorName = "사용자 2",
            imageUrl = "https://via.placeholder.com/150",
            commentCount = 5,
            createdAt = System.currentTimeMillis()
        )
    )




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