package com.example.moneyhub.fragments.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.board.BoardRepository
import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.Post
import com.example.moneyhub.model.sessions.CurrentUserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val repository: BoardRepository,
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _postList = MutableStateFlow<List<Post>>(emptyList())
    val postList: StateFlow<List<Post>> = _postList.asStateFlow()

    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    init {
        loadUser()
        loadPosts()
    }

    private fun loadUser() {
        _currentUser.value = CurrentUserSession.getCurrentUser()
    }

    fun loadPosts() {
        viewModelScope.launch {
            val userGroupId = _currentUser.value?.currentGid ?: return@launch
            repository.getPosts(userGroupId).fold(
                onSuccess = { posts ->
                    _postList.value = posts
                    _uiState.value = UiState(isSuccess = true) },
                onFailure = { exception ->
                    _uiState.value = UiState(error = exception.message)
                }
            )
        }
    }

}