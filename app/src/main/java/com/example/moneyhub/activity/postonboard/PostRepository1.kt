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

    // Mock 데이터를 기반으로 postId에 해당하는 게시글 가져오기
    override suspend fun getPost(postId: String): Post? {
        return mockPosts.find { it.pid == postId }
    }
}