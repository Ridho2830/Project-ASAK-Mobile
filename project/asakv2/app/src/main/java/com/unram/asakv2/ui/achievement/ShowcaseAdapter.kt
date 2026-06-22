package com.unram.asakv2.ui.achievement

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.unram.asakv2.R
import com.unram.asakv2.achievement.AchievementData
import com.unram.asakv2.achievement.AchievementRepository
import java.io.IOException

class ShowcaseAdapter(
    private var slots: List<String?>,
    private var isEditMode: Boolean,
    private var selectedSlot: Int,
    private val repo: AchievementRepository,
    private val onSlotClick: (Int) -> Unit,
    private val onRemoveClick: (Int) -> Unit
) : RecyclerView.Adapter<ShowcaseAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val container: FrameLayout = v.findViewById(R.id.flShowcaseContainer)
        val card: CardView = v.findViewById(R.id.cardShowcase)
        val ivImage: ImageView = v.findViewById(R.id.ivShowcaseImage)
        val tvName: TextView = v.findViewById(R.id.tvShowcaseName)
        val progressBar: ProgressBar = v.findViewById(R.id.pbShowcase)
        val btnPlus: TextView = v.findViewById(R.id.tvShowcasePlus)
        val btnMinus: TextView = v.findViewById(R.id.tvShowcaseMinus)
        val ivGift: ImageView = v.findViewById(R.id.ivShowcaseGift)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_showcase_slot, parent, false)
        return VH(v)
    }

    override fun getItemCount() = 4

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ctx = holder.itemView.context
        val id  = slots.getOrNull(position)
        val def = id?.let { AchievementData.getById(it) }

        if (selectedSlot == position && def == null) {
            holder.card.setCardBackgroundColor(Color.parseColor("#FFF3E0"))
            holder.container.setBackgroundResource(R.drawable.bg_slot_selected)
        } else {
            holder.card.setCardBackgroundColor(Color.WHITE)
            holder.container.setBackgroundResource(0)
        }

        if (def == null) {
            holder.ivImage.visibility    = View.GONE
            holder.tvName.visibility     = View.GONE
            holder.progressBar.visibility = View.GONE
            holder.btnMinus.visibility   = View.GONE
            holder.ivGift.visibility     = View.GONE
            holder.btnPlus.visibility    = View.VISIBLE
        } else {
            holder.btnPlus.visibility    = View.GONE
            holder.ivImage.visibility    = View.VISIBLE
            holder.tvName.visibility     = View.VISIBLE
            holder.progressBar.visibility = View.VISIBLE

            val unlocked = repo.isUnlocked(def.id)
            val claimed  = repo.isClaimed(def.id)

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
            val exp                     = repo.getExp(def.id)
            holder.progressBar.max      = def.expRequired
            holder.progressBar.progress = exp

            holder.btnMinus.visibility = if (isEditMode) View.VISIBLE else View.GONE
            holder.ivGift.visibility   = if (unlocked && !claimed) View.VISIBLE else View.GONE
        }

        holder.btnPlus.setOnClickListener  { onSlotClick(position) }
        holder.btnMinus.setOnClickListener { onRemoveClick(position) }
        holder.card.setOnClickListener     { onSlotClick(position) }
    }

    fun updateSlots(newSlots: List<String?>) { slots = newSlots; notifyDataSetChanged() }
    fun updateEditMode(edit: Boolean)        { isEditMode = edit; notifyDataSetChanged() }
    fun updateSelectedSlot(idx: Int)         { selectedSlot = idx; notifyDataSetChanged() }
}