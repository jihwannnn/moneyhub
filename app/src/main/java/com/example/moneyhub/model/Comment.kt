package com.example.moneyhub.model

// jh 댓글 정보
data class Comment(
    val cid: String = "",           // 댓글 ID
    val pid: String = "",           // 게시글 ID
    val gid: String = "",           // 그룹 ID
    val content: String = "",       // 내용
    val authorId: String = "",      // 작성자 ID
    val authorName: String = "",    // 작성자 이름
    val replyTo: String? = null,    // 답글인 경우 상위 댓글 ID
    val createdAt: Long = System.currentTimeMillis()  // 작성 시간
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "cid" to cid,
            "pid" to pid,
            "gid" to gid,
            "content" to content,
            "authorId" to authorId,
            "authorName" to authorName,
            "replyTo" to replyTo,
            "createdAt" to createdAt
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): Comment {
            return Comment(
                cid = map["cid"] as? String ?: "",
                pid = map["pid"] as? String ?: "",
                gid = map["gid"] as? String ?: "",
                content = map["content"] as? String ?: "",
                authorId = map["authorId"] as? String ?: "",
                authorName = map["authorName"] as? String ?: "",
                replyTo = map["replyTo"] as? String,
                createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis()
            )
        }
    }
}