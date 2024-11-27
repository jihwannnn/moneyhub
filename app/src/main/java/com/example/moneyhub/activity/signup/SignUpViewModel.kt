package com.example.moneyhub.activity.signup

import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(UiState.INITIAL)
    val uiState = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Form States
    private val _name = savedStateHandle.getStateFlow("name", "")
    val name = _name

    private val _email = savedStateHandle.getStateFlow("email", "")
    val email = _email

    private val _phone = savedStateHandle.getStateFlow("phone", "")
    val phone = _phone

    private val _password = savedStateHandle.getStateFlow("password", "")
    val password = _password

    private val _passwordCheck = savedStateHandle.getStateFlow("passwordCheck", "")
    val passwordCheck = _passwordCheck

    // Update Functions
    fun updateName(value: String) = { savedStateHandle["name"] = value }
    fun updateEmail(value: String) = { savedStateHandle["email"] = value }
    fun updatePhone(value: String) = { savedStateHandle["phone"] = value }
    fun updatePassword(value: String) = { savedStateHandle["password"] = value }
    fun updatePasswordCheck(value: String) = { savedStateHandle["passwordCheck"] = value }

    fun signUp() {
        // if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.value = UiState.LOADING

            repository.signUp(
                name = name.value,
                email = email.value,
                phone = phone.value,
                password = password.value
            ).onSuccess {
                _uiState.value = UiState.SUCCESS
            }.onFailure { error ->
                _uiState.value = UiState.ERROR
                _errorMessage.value = error.message
            }
        }
    }

    private fun validateInputs() = when {
        name.value.isBlank() -> {
            _errorMessage.value = "이름을 입력해주세요"
            false
        }

        !isValidEmail(email.value) -> {
            _errorMessage.value = "유효한 이메일을 입력해주세요"
            false
        }

        password.value.length < 6 -> {
            _errorMessage.value = "비밀번호는 6자리 이상이어야 합니다"
            false
        }

        password.value != passwordCheck.value -> {
            _errorMessage.value = "비밀번호가 일치하지 않습니다"
            false
        }
        else -> true
    }

    private fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

}