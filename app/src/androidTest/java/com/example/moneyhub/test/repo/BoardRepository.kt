package com.example.moneyhub.test.repo

import com.example.moneyhub.model.Comment
import com.example.moneyhub.model.Post

interface BoardRepository {

    // 게시글 생성
    suspend fun createPost(post: Post): Result<Unit>

    // 게시글 업데이트
    suspend fun updatePost(post: Post): Result<Unit>

    // 게시글 삭제
    suspend fun deletePost(gid: String, pid: String): Result<Unit>

    // 게시글 가져오기
    suspend fun getPosts(gid: String): Result<List<Post>>

    // 댓글 생성
    suspend fun addComment(comment: Comment): Result<Unit>

    // 댓글 업데이트
    suspend fun updateComment(comment: Comment): Result<Unit>

    // 댓글 삭제
    suspend fun deleteComment(gid: String, pid: String, cid: String): Result<Unit>

    // 댓글 가져오기
    suspend fun getComments(gid: String, pid: String): Result<List<Comment>>
}