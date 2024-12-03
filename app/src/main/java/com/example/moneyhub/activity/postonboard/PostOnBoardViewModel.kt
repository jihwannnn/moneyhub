package com.example.moneyhub.activity.postonboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostOnBoardViewModel @Inject constructor(
    private val repository: PostRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()


    // Post submission logic
    fun post(title: String, content: String, authorId: String, authorName: String, groupId: String, imageUri: String) {
        if (!validateInputs(title, content)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val post = Post(
                pid = System.currentTimeMillis().toString(), // Unique ID
                gid = groupId,
                title = title,
                content = content,
                authorId = authorId,
                authorName = authorName,
                imageUrl = imageUri,
                commentCount = 0,
                createdAt = System.currentTimeMillis()
            )

            try{
                repository.createPost(post).fold(
                    onSuccess = {
                        _uiState.update { it.copy(
                            isLoading = false,
                            isSuccess = true,
                            error = null
                        ) }
                    },
                    onFailure = { throwable ->
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = throwable.message
                        ) }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message
                ) }
            }


        }
    }

    private fun validateInputs(title: String, content: String): Boolean = when {
        title.isBlank() -> {
            _uiState.update { it.copy(error = "제목을 입력해주세요" ) }
            false
        }

        content.isBlank() -> {
            _uiState.update { it.copy(error = "내용을 입력해주세요" ) }
            false
        }

        else -> true
    }

    private val _currentPost = MutableStateFlow<Post?>(null)
    val currentPost = _currentPost.asStateFlow()

    fun fetchPost(postId: String) {
        viewModelScope.launch {
            try {
                val post = repository.getPost(postId) // 게시글 데이터를 가져옴
                _currentPost.value = post
            } catch (e: Exception) {
                _currentPost.value = null // 에러 발생 시 null로 설정
            }
        }
    }
}