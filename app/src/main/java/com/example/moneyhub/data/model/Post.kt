package com.example.moneyhub.data.model

data class Post(
    val pid: String = "",           // 게시글 ID
    val gid: String = "",           // 그룹 ID
    val title: String = "",         // 제목
    val content: String = "",       // 내용
    val authorId: String = "",      // 작성자 ID
    val authorName: String = "",    // 작성자 이름
    val commentCount: Int = 0,      // 댓글 수
    val createdAt: Long = System.currentTimeMillis()  // 작성 시간
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "pid" to pid,
            "gid" to gid,
            "title" to title,
            "content" to content,
            "authorId" to authorId,
            "authorName" to authorName,
            "commentCount" to commentCount,
            "createdAt" to createdAt
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): Post {
            return Post(
                pid = map["pid"] as? String ?: "",
                gid = map["gid"] as? String ?: "",
                title = map["title"] as? String ?: "",
                content = map["content"] as? String ?: "",
                authorId = map["authorId"] as? String ?: "",
                authorName = map["authorName"] as? String ?: "",
                commentCount = (map["commentCount"] as? Long)?.toInt() ?: 0,
                createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis()
            )
        }
    }
}