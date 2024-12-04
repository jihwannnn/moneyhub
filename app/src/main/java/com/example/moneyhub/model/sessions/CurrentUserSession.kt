package com.example.moneyhub.model.sessions

import com.example.moneyhub.model.CurrentUser

object CurrentUserSession {
    private var currentUser: CurrentUser? = null

    fun setCurrentUser(user: CurrentUser) {
        currentUser = user
    }

    fun getCurrentUser(): CurrentUser? = currentUser

    fun clearCurrentUser() {
        currentUser = null
    }

    fun isLoggedIn(): Boolean = currentUser != null
}