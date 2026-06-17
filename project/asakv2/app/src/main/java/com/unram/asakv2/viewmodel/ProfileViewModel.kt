package com.unram.asakv2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.unram.asakv2.model.User
import com.unram.asakv2.repository.UserRepository
import com.unram.asakv2.utils.SessionManager
import kotlinx.coroutines.launch

/**
 * ProfileViewModel — Load profil, update nama user.
 * Mengelola data profil pengguna dan pembaruan informasi.
 * [RIDHO]
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository()
    private val sessionManager = SessionManager(application)

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _updateResult = MutableLiveData<Result<Boolean>>()
    val updateResult: LiveData<Result<Boolean>> = _updateResult

    fun loadProfile(userId: String) {
        userRepository.getUser(userId) { result ->
            if (result.isSuccess) {
                _user.postValue(result.getOrNull())
            }
        }
    }

    fun updateName(userId: String, newName: String, photoUrl: String) {
        userRepository.updateUserName(userId, newName) { result ->
            if (result.isSuccess) {
                viewModelScope.launch {
                    sessionManager.saveSession(userId, newName, photoUrl)
                }
                loadProfile(userId)
            }
            _updateResult.postValue(result)
        }
    }
}
