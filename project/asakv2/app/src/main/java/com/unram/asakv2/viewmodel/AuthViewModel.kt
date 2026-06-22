package com.unram.asakv2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.unram.asakv2.repository.AuthRepository
import com.unram.asakv2.utils.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()
    private val sessionManager = SessionManager(application)

    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun firebaseAuthWithGoogle(idToken: String) {
        _isLoading.value = true
        authRepository.firebaseAuthWithGoogle(idToken) { result ->
            if (result.isSuccess) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    viewModelScope.launch {
                        sessionManager.saveSession(
                            userId = currentUser.uid,
                            userName = currentUser.displayName ?: "Pelajar",
                            photoUrl = currentUser.photoUrl?.toString() ?: ""
                        )
                        _isLoading.postValue(false)
                        _loginResult.postValue(result)
                    }
                } else {
                    _isLoading.postValue(false)
                    _loginResult.postValue(result)
                }
            } else {
                _isLoading.postValue(false)
                _loginResult.postValue(result)
            }
        }
    }

    fun logout() {
        authRepository.logout()
        viewModelScope.launch {
            sessionManager.clearSession()
        }
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
}
