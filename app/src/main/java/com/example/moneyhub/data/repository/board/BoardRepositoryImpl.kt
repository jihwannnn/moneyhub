package com.example.moneyhub.data.repository.board

import com.google.firebase.firestore.FirebaseFirestore
import com.example.moneyhub.model.Comment
import com.example.moneyhub.model.Post
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor() : BoardRepository {
    private val db = FirebaseFirestore.getInstance()

    // post 생성
    override suspend fun createPost(post: Post): Result<Unit> {
        return try {
            val gid = post.gid

            // 새 게시글 참조 생성 (ID 생성)
            val postRef = db.collection("posts_group")
                .document(gid)
                .collection("posts")
                .document()

            // 생성된 ID를 포함한 새 Post 객체 생성
            val newPost = post.copy(
                pid = postRef.id,
            )

            // Firestore에 저장
            postRef.set(newPost.toMap()).await()

            // 생성된 ID 반환
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // post 업데이트
    override suspend fun updatePost(post: Post): Result<Unit> {
        return try {
            val gid = post.gid
            val pid = post.pid

            // 게시글 참조
            val postRef = db.collection("posts_group")
                .document(gid)
                .collection("posts")
                .document(pid)

            // 업데이트
            postRef.set(post.toMap()).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // post 삭제
    override suspend fun deletePost(
        gid: String,
        pid: String
    ): Result<Unit> {
        return try {
            // 게시글 참조
            val postRef = db.collection("posts_group")
                .document(gid)
                .collection("posts")
                .document(pid)

            // 댓글들
            val commentsRef = db.collection("comments_group")
                .document(gid)
                .collection("comments_post")
                .document(pid)

            val commentsCollection = commentsRef.collection("comments")

            // 배치 초기화
            val batch = db.batch()

            // 모든 댓글 삭제
            val commentSnapshots = commentsCollection.get().await()
            commentSnapshots.documents.forEach { doc ->
                batch.delete(doc.reference)
            }

            // 댓글 컬렉션 삭제
            batch.delete(commentsRef)

            // 게시글 삭제
            batch.delete(postRef)

            // 배치 작업 실행
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // post 가져오기
    override suspend fun getPosts(gid: String): Result<List<Post>> {
        return try {
            // 게시글 참조
            val postsCollection = db.collection("posts_group")
                .document(gid)
                .collection("posts")

            // 쿼리 실행
            val querySnapshot = postsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val posts = querySnapshot.documents.mapNotNull { doc ->
                doc.data?.let { Post.fromMap(it) }
            }

            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // comment 생성
    override suspend fun addComment(comment: Comment): Result<Unit> {
        return try {
            val gid = comment.gid
            val pid = comment.pid

            // 새 댓글 문서 참조 생성
            val commentRef = db.collection("comments_group")
                .document(gid)
                .collection("comments_post")
                .document(pid)
                .collection("comments")
                .document()

            // 생성된 ID를 포함한 새 Comment 객체 생성
            val newComment = comment.copy(
                cid = commentRef.id,
            )

            // 동시 처리를 위한 트랜잭션
            db.runTransaction { transaction ->

                // 게시글 참조
                val postRef = db.collection("posts_group")
                    .document(gid)
                    .collection("posts")
                    .document(pid)

                // 게시글 commentCount 가져오기
                val postDoc = transaction.get(postRef)
                val currentCommentCount = postDoc.getLong("commentCount") ?: 0

                // 댓글 저장과 댓글 수 업데이트
                transaction.set(commentRef, newComment.toMap())
                transaction.update(postRef, "commentCount", currentCommentCount + 1)
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateComment(comment: Comment): Result<Unit> {
        return try {
            val gid = comment.gid
            val pid = comment.pid
            val cid = comment.cid

            // 댓글 참조
            val commentRef = db.collection("comments_group")
                .document(gid)
                .collection("comments_post")
                .document(pid)
                .collection("comments")
                .document(cid)

            // 업데이트
            commentRef.set(comment.toMap()).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteComment(
        gid: String,
        pid: String,
        cid: String
    ): Result<Unit> {
        return try {
            // 게시글 참조 초기화
            val postRef = db.collection("posts_group")
                .document(gid)
                .collection("posts")
                .document(pid)

            // 댓글 참조 초기화
            val commentRef = db.collection("comments_group")
                .document(gid)
                .collection("comments_post")
                .document(pid)
                .collection("comments")
                .document(cid)

            // 트랜잭션으로 댓글 삭제와 댓글 수 업데이트
            db.runTransaction { transaction ->
                val postDoc = transaction.get(postRef)
                val currentCommentCount = postDoc.getLong("commentCount") ?: 0

                transaction.delete(commentRef)
                transaction.update(postRef, "commentCount", currentCommentCount - 1)
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getComments(
        gid: String,
        pid: String
    ): Result<List<Comment>> {
        return try {
            // 컬렉션 참조 초기화
            val commentsCollection = db.collection("comments_group")
                .document(gid)
                .collection("comments_post")
                .document(pid)
                .collection("comments")

            // 쿼리 실행
            val querySnapshot = commentsCollection
                .orderBy("createdAt")
                .get()
                .await()

            val comments = querySnapshot.documents.mapNotNull { doc ->
                doc.data?.let { Comment.fromMap(it) }
            }

            Result.success(comments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}