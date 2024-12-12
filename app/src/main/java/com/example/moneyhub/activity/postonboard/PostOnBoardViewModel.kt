package com.example.moneyhub.activity.postonboard

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.board.BoardRepository
import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.Post
import com.example.moneyhub.model.sessions.CurrentUserSession
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resumeWithException


@HiltViewModel
class PostOnBoardViewModel @Inject constructor(
    private val repository: BoardRepository,
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        _currentUser.value = CurrentUserSession.getCurrentUser()
    }


    // Post submission logic
    fun post(title: String, content: String, imageUri: Uri?) {
        if (!validateInputs(title, content)) return

        val user = _currentUser.value ?: run {
            _uiState.update { it.copy(error = "사용자 정보를 찾을 수 없습니다") }
            return
        }


        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val imageUrl = if (imageUri != null) {
                    uploadImageToFirebase(imageUri)
                } else {
                    ""
                }

                val post = Post(
                    pid = UUID.randomUUID().toString(),
                    gid = user.currentGid,
                    title = title,
                    content = content,
                    authorId = user.id,
                    authorName = user.name,
                    imageUrl = imageUrl,
                    commentCount = 0,
                    createdAt = System.currentTimeMillis()
                )

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

    private suspend fun uploadImageToFirebase(imageUri: Uri): String {
        return suspendCancellableCoroutine { cont ->
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        cont.resume(uri.toString(), null)
                    }.addOnFailureListener { exception ->
                        cont.resumeWithException(exception)
                    }
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
        }
    }
}