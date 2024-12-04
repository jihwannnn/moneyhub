package com.example.moneyhub.activity.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.auth.AuthRepository
import com.example.moneyhub.data.repository.group.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun signUp(name: String, email: String, phone: String, password: String, passwordCheck: String) {
        // if (!validateInputs(name, email, password, passwordCheck)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                authRepository.signUp(
                    name = name,
                    email = email,
                    phone = phone,
                    password = password
                ).fold(
                    onSuccess = { uid ->
                        groupRepository.createUserGroup(uid).fold(
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
                                    error = "계정은 생성되었으나 그룹 초기화에 실패했습니다: ${throwable.message}"
                                ) }
                            }
                        )
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

    private fun validateInputs(name: String, email: String, password: String, passwordCheck: String) = when {
        name.isBlank() -> {
            _uiState.update { it.copy(error = "이름을 입력해주세요") }
            false
        }
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
            _uiState.update { it.copy(error = "유효한 이메일을 입력해주세요") }
            false
        }
        password.length < 6 -> {
            _uiState.update { it.copy(error = "비밀번호는 6자리 이상이어야 합니다") }
            false
        }
        password != passwordCheck -> {
            _uiState.update { it.copy(error = "비밀번호가 일치하지 않습니다") }
            false
        }
        else -> true
    }
}
