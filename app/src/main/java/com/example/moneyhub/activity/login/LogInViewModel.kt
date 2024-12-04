package com.example.moneyhub.activity.login


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.auth.AuthRepository
import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.sessions.CurrentUserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                authRepository.signIn(email, password).fold(
                    onSuccess = { user ->
                        CurrentUserSession.setCurrentUser(user)
                        _currentUser.value = user
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
}