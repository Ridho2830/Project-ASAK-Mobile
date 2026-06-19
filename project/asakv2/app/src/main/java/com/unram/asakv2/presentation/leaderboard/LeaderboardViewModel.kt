package com.unram.asakv2.presentation.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unram.asakv2.data.remote.RankingItem
import com.unram.asakv2.data.repository.LeaderboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed class LeaderboardUiState {
    object Loading : LeaderboardUiState()
    data class Success(
        val leagueName: String,
        val resetInSeconds: Long,
        val rankings: List<RankingItem>,
        val myRanking: RankingItem?
    ) : LeaderboardUiState()
    data class Error(val message: String) : LeaderboardUiState()
}

class LeaderboardViewModel(
    private val repository: LeaderboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LeaderboardUiState>(LeaderboardUiState.Loading)
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    // In a real app, this should come from DataStore / Firebase Auth
    private val currentUserId = "u1" 

    init {
        fetchLeaderboard("Bronze")
    }

    fun fetchLeaderboard(league: String) {
        viewModelScope.launch {
            _uiState.value = LeaderboardUiState.Loading
            repository.getWeeklyLeaderboard(league)
                .catch { e ->
                    _uiState.value = LeaderboardUiState.Error(e.message ?: "Unknown Error")
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { response ->
                            val rankings = response.data.rankings
                            val myRanking = rankings.find { it.user_id == currentUserId }
                            
                            _uiState.value = LeaderboardUiState.Success(
                                leagueName = response.data.league_name,
                                resetInSeconds = response.data.reset_in_seconds,
                                rankings = rankings,
                                myRanking = myRanking
                            )
                        },
                        onFailure = { e ->
                            _uiState.value = LeaderboardUiState.Error(e.message ?: "Failed to load")
                        }
                    )
                }
        }
    }
}
