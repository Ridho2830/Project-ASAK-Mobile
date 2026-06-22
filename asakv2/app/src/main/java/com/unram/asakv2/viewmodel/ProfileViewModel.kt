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
 * ProfileViewModel — Load profil, update nama, tagline, dan foto user.
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository()
    private val sessionManager = SessionManager(application)

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _updateResult = MutableLiveData<Result<Boolean>>()
    val updateResult: LiveData<Result<Boolean>> = _updateResult

    private val _statistics = MutableLiveData<com.unram.asakv2.data.remote.StatisticData?>()
    val statistics: LiveData<com.unram.asakv2.data.remote.StatisticData?> = _statistics

    fun loadProfile(userId: String) {
        userRepository.getUser(userId) { result ->
            if (result.isSuccess) {
                val userObj = result.getOrNull()
                if (userObj != null) {
                    val localStage = com.unram.asakv2.utils.ProgressManager.getCurrentStage(getApplication())
                    val localBagian = com.unram.asakv2.utils.ProgressManager.getCurrentBagian(getApplication())
                    val isLocalAhead = localStage > userObj.currentStage || 
                        (localStage == userObj.currentStage && localBagian > userObj.currentBagian)

                    if (isLocalAhead) {
                        userRepository.updateQuizResult(
                            userId = userId,
                            xpGained = 0,
                            accuracy = 0.0,
                            isWriting = false,
                            isSpeaking = false,
                            streak = userObj.streak,
                            completedStageId = 0,
                            currentStage = localStage,
                            currentBagian = localBagian
                        ) {
                            val updatedUser = userObj.copy(
                                currentStage = localStage,
                                currentBagian = localBagian
                            )
                            _user.postValue(updatedUser)
                        }
                    } else {
                        com.unram.asakv2.utils.ProgressManager.syncLocalProgress(
                            getApplication(),
                            userObj.currentStage,
                            userObj.currentBagian
                        )
                        _user.postValue(userObj)
                    }
                } else {
                    _user.postValue(userObj)
                }
            }
        }
        viewModelScope.launch {
            try {
                val response = com.unram.asakv2.network.RetrofitClient.apiService.getUserStatistics(userId)
                if (response.isSuccessful && response.body() != null) {
                    _statistics.postValue(response.body()!!.data)
                }
            } catch (e: Exception) {
                // Ignore or handle
            }
        }
    }

    fun resetProgress(userId: String, callback: (Result<Boolean>) -> Unit) {
        userRepository.resetUserProgress(userId) { result ->
            if (result.isSuccess) {
                com.unram.asakv2.utils.ProgressManager.resetProgress(getApplication())
                loadProfile(userId)
                callback(Result.success(true))
            } else {
                callback(Result.failure(result.exceptionOrNull() ?: Exception("Gagal mereset progress")))
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

    fun updateTagline(userId: String, tagline: String) {
        userRepository.updateUserTagline(userId, tagline) { result ->
            if (result.isSuccess) {
                loadProfile(userId)
            }
            _updateResult.postValue(result)
        }
    }

    fun updatePhoto(userId: String, photoUrl: String) {
        userRepository.updateUserPhoto(userId, photoUrl) { result ->
            if (result.isSuccess) {
                viewModelScope.launch {
                    sessionManager.saveSession(userId, _user.value?.name ?: "", photoUrl)
                }
                loadProfile(userId)
            }
            _updateResult.postValue(result)
        }
    }
}
