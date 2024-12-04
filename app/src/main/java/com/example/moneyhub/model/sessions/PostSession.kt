package com.example.moneyhub.model.sessions


import com.example.moneyhub.model.Post

object PostSession {
    private var currentPost: Post? = null

    fun setPost(post: Post) {
        currentPost = post
    }

    fun getCurrentPost(): Post = currentPost ?: Post()

    fun clearCurrentUser() {
        currentPost = null
    }

}