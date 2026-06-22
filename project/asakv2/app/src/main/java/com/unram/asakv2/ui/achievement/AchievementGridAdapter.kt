package com.unram.asakv2.ui.achievement

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.unram.asakv2.R
import com.unram.asakv2.achievement.AchievementDef
import com.unram.asakv2.achievement.AchievementRepository
import java.io.IOException

class AchievementGridAdapter(
    private var items: List<AchievementDef>,
    private val repo: AchievementRepository,
    private val onItemClick: (AchievementDef) -> Unit
) : RecyclerView.Adapter<AchievementGridAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val ivImage: ImageView = v.findViewById(R.id.ivAchievementImage)
        val tvName: TextView = v.findViewById(R.id.tvAchievementName)
        val progressBar: ProgressBar = v.findViewById(R.id.pbAchievement)
        val tvProgress: TextView = v.findViewById(R.id.tvAchievementProgress)
        val ivGift: ImageView = v.findViewById(R.id.ivAchievementGift)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_achievement, parent, false)
        return VH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val def      = items[position]
        val ctx      = holder.itemView.context
        val unlocked = repo.isUnlocked(def.id)
        val claimed  = repo.isClaimed(def.id)
        val exp      = repo.getExp(def.id)

        val assetPath = if (unlocked) def.assetColorPath else def.assetGrayPath
        try {
            val inputStream = ctx.assets.open(assetPath)
            val drawable    = Drawable.createFromStream(inputStream, null)
            holder.ivImage.setImageDrawable(drawable)
            inputStream.close()
        } catch (e: IOException) {
            holder.ivImage.setImageResource(R.drawable.ic_achievement)
        }

        holder.tvName.text          = def.name
        holder.progressBar.max      = def.expRequired
        holder.progressBar.progress = exp
        holder.tvProgress.text      = "$exp / ${def.expRequired}"
        holder.ivGift.visibility    = if (unlocked && !claimed) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener { onItemClick(def) }
    }

    fun updateList(newItems: List<AchievementDef>) {
        items = newItems
        notifyDataSetChanged()
    }
}