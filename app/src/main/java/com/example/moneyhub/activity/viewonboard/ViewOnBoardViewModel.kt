package com.example.moneyhub.activity.viewonboard

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.board.BoardRepository
import com.example.moneyhub.model.Comment
import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.Post
import com.example.moneyhub.model.sessions.CurrentUserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
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

    private val _commentList = MutableStateFlow<List<Comment>>(emptyList())
    val commentList: StateFlow<List<Comment>> = _commentList.asStateFlow()

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
                loadComments(post)  // 게시글 정보 로딩 후 댓글 불러오기
            } catch (e: Exception) {
                _currentPost.value = null // 에러 발생 시 null로 설정
            }
        }
    }

    // 게시글 삭제 로직
    fun deletePost(post: Post) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.deletePost(post.gid, post.pid).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true, error = null) }
                },
                onFailure = { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message) }
                }
            )
        }
    }

    // 게시글 수정 로직
    fun editPost(updatedPost: Post) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.updatePost(updatedPost).fold(
                onSuccess = {
                    _currentPost.value = updatedPost
                    _uiState.update { it.copy(isLoading = false, isSuccess = true, error = null) }
                },
                onFailure = { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message) }
                }
            )
        }
    }

    fun loadComments(post: Post) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getComments(post.gid, post.pid).fold(
                onSuccess = { comments ->
                    _commentList.value = comments
                    _uiState.update { it.copy(isLoading = false) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    fun addComment(content: String) {
        val user = _currentUser.value ?: return
        val post = _currentPost.value ?: return

        if (content.isBlank()) {
            _uiState.value = UiState(error = "댓글 내용을 입력해주세요.")
            return
        }

        val comment = Comment(
            gid = post.gid,
            pid = post.pid,
            content = content,
            authorId = user.id,
            authorName = user.name,
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            repository.addComment(comment).fold(
                onSuccess = {
                    loadComments(post)  // 댓글 생성 후 다시 로딩
                },
                onFailure = { e ->
                    _uiState.value = UiState(error = e.message)
                }
            )
        }
    }

    fun editComment(targetComment: Comment, newContent: String) {
        if (newContent.isBlank()) {
            _uiState.value = UiState(error = "댓글 내용을 입력해주세요.")
            return
        }

        val updatedComment = targetComment.copy(content = newContent)

        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            repository.updateComment(updatedComment).fold(
                onSuccess = {
                    val post = _currentPost.value ?: return@fold
                    loadComments(post)
                },
                onFailure = { e ->
                    _uiState.value = UiState(error = e.message)
                }
            )
        }
    }

    fun deleteComment(targetComment: Comment) {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            repository.deleteComment(targetComment.gid, targetComment.pid, targetComment.cid)
                .fold(
                    onSuccess = {
                        val post = _currentPost.value ?: return@fold
                        loadComments(post)
                    },
                    onFailure = { e ->
                        _uiState.value = UiState(error = e.message)
                    }
                )
        }
    }

}