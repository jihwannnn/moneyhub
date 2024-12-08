package com.example.moneyhub.activity.viewonboard

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.board.BoardRepository
import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.Post
import com.example.moneyhub.model.sessions.CurrentUserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ViewOnBoardViewModel @Inject constructor(
    private val repository: BoardRepository,
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    private val _currentPost = MutableStateFlow<Post?>(null)
    val currentPost = _currentPost.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        _currentUser.value = CurrentUserSession.getCurrentUser()
    }

    fun fetchPost(post: Post) {
        viewModelScope.launch {
            try {
                _currentPost.value = post
            } catch (e: Exception) {
                _currentPost.value = null // 에러 발생 시 null로 설정
            }
        }
    }

}