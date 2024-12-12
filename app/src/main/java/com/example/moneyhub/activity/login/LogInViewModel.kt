package com.example.moneyhub.activity.login


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.auth.AuthRepository
import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.sessions.CurrentUserSession
import com.google.firebase.Firebase
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions
import com.google.firebase.messaging.FirebaseMessaging
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

    private val functions: FirebaseFunctions = Firebase.functions


    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    private val _fcmInitialized = MutableStateFlow(false)
    val fcmInitialized: StateFlow<Boolean> = _fcmInitialized.asStateFlow()


    init{
        checkLoggedIn()
    }


    private fun checkLoggedIn() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                authRepository.getCurrentUser().fold(
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

    fun initializeFcm(context: Context) {
        viewModelScope.launch {
            try {
                val sharedPrefs = context.getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
                val savedToken = sharedPrefs.getString("fcm_token", null)

                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val newToken = task.result

                        if (savedToken != newToken) {
                            // Save new token to SharedPreferences
                            sharedPrefs.edit().putString("fcm_token", newToken).apply()

                            // Update token on server
                            updateFcmTokenOnServer(newToken)
                        }
                        _fcmInitialized.value = true
                    } else {
                        Log.w("FCM", "Token fetch failed", task.exception)
                        _fcmInitialized.value = false
                    }
                }
            } catch (e: Exception) {
                Log.e("FCM", "Failed to initialize FCM", e)
                _fcmInitialized.value = false
            }
        }
    }

    private fun updateFcmTokenOnServer(token: String) {
        viewModelScope.launch {
            try {
                functions
                    .getHttpsCallable("updateFcmToken")
                    .call(hashMapOf("token" to token))
                    .addOnSuccessListener {
                        Log.d("FCM", "Token updated: $token")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FCM", "Failed to update token", e)
                    }
            } catch (e: Exception) {
                Log.e("FCM", "Error updating FCM token", e)
            }
        }
    }

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