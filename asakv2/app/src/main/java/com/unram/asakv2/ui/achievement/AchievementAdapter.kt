package com.unram.asakv2.ui.achievement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unram.asakv2.R
import com.unram.asakv2.model.UserAchievement

/**
 * AchievementAdapter — RecyclerView adapter untuk grid achievement.
 * Menampilkan item achievement dengan state terkunci/terbuka.
 * [DESTI]
 */
class AchievementAdapter(
    private var achievements: List<UserAchievement> = emptyList(),
    private val onItemClick: (UserAchievement) -> Unit
) : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    inner class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO: Bind views (icon, title, locked overlay)
        fun bind(achievement: UserAchievement) {
            itemView.setOnClickListener { onItemClick(achievement) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_achievement, parent, false)
        return AchievementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(achievements[position])
    }

    override fun getItemCount(): Int = achievements.size

    fun updateData(newAchievements: List<UserAchievement>) {
        achievements = newAchievements
        notifyDataSetChanged()
    }
}
