package com.unram.asakv2.achievement

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class AchievementViewModel(app: Application) : AndroidViewModel(app) {

    val repo = AchievementRepository(app)

    private val _achievements = MutableLiveData<List<AchievementDef>>()
    val achievements: LiveData<List<AchievementDef>> = _achievements

    private val _showcaseSlots = MutableLiveData<List<String?>>()
    val showcaseSlots: LiveData<List<String?>> = _showcaseSlots

    private val _hasUnclaimed = MutableLiveData<Boolean>()
    val hasUnclaimed: LiveData<Boolean> = _hasUnclaimed

    var sortType: SortType = SortType.DEFAULT
    var sortAscending: Boolean = true

    enum class SortType { DEFAULT, AZ, PROGRESS, UNLOCK }

    init { refresh() }

    fun refresh() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            repo.loadFromFirestore(currentUser.uid) { result ->
                if (result.isSuccess) {
                    _achievements.postValue(buildSortedList())
                    _showcaseSlots.postValue(repo.getShowcaseSlots())
                    _hasUnclaimed.postValue(repo.hasUnclaimed())
                }
            }
        } else {
            _achievements.value = buildSortedList()
            _showcaseSlots.value = repo.getShowcaseSlots()
            _hasUnclaimed.value = repo.hasUnclaimed()
        }
    }

    // Renamed dari setSortType → changeSortType untuk hindari clash dengan Kotlin setter
    fun changeSortType(type: SortType) {
        sortType = type
        if (type == SortType.DEFAULT) sortAscending = true
        _achievements.value = buildSortedList()
    }

    fun toggleSortDirection() {
        if (sortType == SortType.DEFAULT) return
        sortAscending = !sortAscending
        _achievements.value = buildSortedList()
    }

    private fun buildSortedList(): List<AchievementDef> {
        val base = AchievementData.all
        val sorted = when (sortType) {
            SortType.DEFAULT  -> base
            SortType.AZ       -> base.sortedBy { it.name }
            SortType.PROGRESS -> base.sortedByDescending { repo.getExp(it.id).toFloat() / it.expRequired }
            SortType.UNLOCK   -> base.sortedByDescending { if (repo.isUnlocked(it.id)) 1 else 0 }
        }
        return if (sortAscending || sortType == SortType.DEFAULT) sorted else sorted.reversed()
    }

    fun setShowcaseSlot(index: Int, id: String?) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        repo.setShowcaseSlot(index, id, currentUser.uid) { result ->
            if (result.isSuccess) {
                val selectedIds = repo.getShowcaseSlots().filterNotNull()
                val selectedTitle = if (selectedIds.isNotEmpty()) {
                    val firstId = selectedIds.first()
                    AchievementData.getById(firstId)?.name ?: "Pelajar"
                } else {
                    "Belum Ada"
                }

                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("users").document(currentUser.uid)
                    .update("selectedAchievement", selectedTitle)
                    .addOnCompleteListener {
                        refresh()
                    }
            }
        }
    }

    fun claimReward(id: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        repo.setClaimed(id, currentUser.uid) { result ->
            if (result.isSuccess) {
                refresh()
            }
        }
    }

    fun addExp(id: String, amount: Int) {
        val newlyUnlocked = repo.addExp(id, amount)
        if (newlyUnlocked && !repo.isNotified(id)) {
            repo.setNotified(id)
            AchievementUnlockManager.enqueue(id)
        }
        refresh()
    }
}
