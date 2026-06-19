package com.unram.asakv2.presentation.leaderboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.unram.asakv2.data.remote.RankingItem
import com.unram.asakv2.databinding.ItemLeaderboardRowBinding

class LeaderboardAdapter : ListAdapter<RankingItem, LeaderboardAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ItemLeaderboardRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RankingItem) {
            binding.tvRank.text = item.rank_position.toString()
            binding.tvUsername.text = item.username
            binding.tvXp.text = "${item.total_xp} XP"
            
            // Highlight styling based on rank
            when (item.rank_position) {
                1 -> binding.tvRank.setTextColor(android.graphics.Color.parseColor("#FFD700")) // Gold
                2 -> binding.tvRank.setTextColor(android.graphics.Color.parseColor("#C0C0C0")) // Silver
                3 -> binding.tvRank.setTextColor(android.graphics.Color.parseColor("#CD7F32")) // Bronze
                else -> binding.tvRank.setTextColor(android.graphics.Color.WHITE)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLeaderboardRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<RankingItem>() {
            override fun areItemsTheSame(oldItem: RankingItem, newItem: RankingItem): Boolean {
                return oldItem.user_id == newItem.user_id
            }

            override fun areContentsTheSame(oldItem: RankingItem, newItem: RankingItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
