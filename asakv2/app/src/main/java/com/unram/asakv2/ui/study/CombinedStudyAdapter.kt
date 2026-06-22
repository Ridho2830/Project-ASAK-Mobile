package com.unram.asakv2.ui.study

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.unram.asakv2.R
import com.unram.asakv2.utils.ProgressManager
import com.bumptech.glide.Glide
import java.io.IOException

import com.unram.asakv2.ar.ArModelMapper

class CombinedStudyAdapter(
    private val listItems: List<StudyItem>,
    private val user: com.unram.asakv2.model.User?,
    private val onActionTriggered: (StudyItem, String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_HURUF = 1
    private val TYPE_WISATA_BUDAYA = 2

    override fun getItemViewType(position: Int): Int {
        return when (listItems[position].type) {
            StudyType.HEADER_HURUF, StudyType.HEADER_WISATA, StudyType.HEADER_BUDAYA -> TYPE_HEADER
            StudyType.HURUF -> TYPE_HURUF
            StudyType.WISATA, StudyType.BUDAYA -> TYPE_WISATA_BUDAYA
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_study_header, parent, false)
                HeaderViewHolder(view)
            }
            TYPE_HURUF -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_huruf_grid, parent, false)
                HurufViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_wisata_budaya, parent, false)
                WisataBudayaViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = listItems[position]
        val context = holder.itemView.context

        when (holder) {
            is HeaderViewHolder -> {
                holder.tvHeaderTitle.text = item.name
            }

            is HurufViewHolder -> {
                holder.tvName.text = item.name
                holder.tvSub.text = "[${item.id.lowercase()}]"

                // Load gambar aksara secara asinkron menggunakan Glide agar tidak memblokir main thread
                Glide.with(context)
                    .load("file:///android_asset/aksara/gambar/${item.id.lowercase()}.png")
                    .placeholder(R.drawable.ic_book)
                    .error(R.drawable.ic_book)
                    .into(holder.ivAksara)

                val currentStageUser = user?.currentStage ?: ProgressManager.getCurrentStage(context)
                val currentBagianUser = user?.currentBagian ?: ProgressManager.getCurrentBagian(context)
                val isUnlocked = currentStageUser > item.levelRequired ||
                                 (currentStageUser == item.levelRequired && currentBagianUser > 4)

                if (isUnlocked) {
                    holder.card.setCardBackgroundColor(Color.WHITE)
                    holder.ivLock.visibility = View.GONE
                    holder.card.setOnClickListener {
                        // Tap kartu = putar audio
                        try {
                            val mediaPlayer = MediaPlayer()
                            val afd = context.assets.openFd(
                                "aksara/audio/${item.id.lowercase().trim()}.mp3"
                            )
                            mediaPlayer.setDataSource(
                                afd.fileDescriptor, afd.startOffset, afd.length
                            )
                            afd.close()
                            mediaPlayer.prepare()
                            mediaPlayer.start()
                            mediaPlayer.setOnCompletionListener { mp -> mp.release() }
                        } catch (e: IOException) {
                            Toast.makeText(context, "Audio tidak ditemukan", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } else {
                    holder.card.setCardBackgroundColor(Color.parseColor("#E0E0E0"))
                    holder.ivLock.visibility = View.VISIBLE
                    holder.card.setOnClickListener {
                        Toast.makeText(
                            context,
                            "Selesaikan sub-stage AR di Stage ${item.levelRequired} terlebih dahulu!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                holder.btnMenu.setOnClickListener { view ->
                    val popup = PopupMenu(context, view)
                    if (isUnlocked) {
                        popup.menu.add("Menulis")
                        popup.menu.add("Mengucap")
                        popup.menu.add("AR (Dummy)")
                    } else {
                        popup.menu.add("Terkunci")
                    }
                    popup.setOnMenuItemClickListener { menuItem ->
                        val txt = menuItem.title.toString()
                        when (txt) {
                            "Menulis" -> onActionTriggered(item, "Menulis")
                            "Mengucap" -> onActionTriggered(item, "Mengucap")
                            "AR (Dummy)" -> onActionTriggered(item, "AR_HURUF")
                        }
                        true
                    }
                    popup.show()
                }
            }

            is WisataBudayaViewHolder -> {
                holder.tvTitle.text = item.name

                val code = when (item.achievementKey) {
                    "ach_terjun" -> "air_terjun"
                    "ach_rinjani" -> "gn_rinjani"
                    "ach_sade" -> "desa_sade"
                    "ach_kuta" -> "pantai_kuta"
                    "ach_sirkuit" -> "sirkuit"
                    "ach_museum" -> "museum"
                    "ach_bayan" -> "masjid_bayan"
                    "ach_selaparang" -> "makam"
                    "ach_nyale" -> "bau_nyale"
                    "ach_begawe" -> "begawe"
                    "ach_gendang" -> "gendang_beleq"
                    "ach_gerabah" -> "gerabah"
                    "ach_menutu" -> "menutu"
                    "ach_merariq" -> "mrariq"
                    "ach_tenun" -> "tenun"
                    "ach_begasingan" -> "begasingan"
                    "ach_santekan" -> "santekan"
                    "ach_nyongkolan" -> "nyongkolan"
                    "ach_misoq" -> "bisoq_meniq"
                    "ach_gula" -> "gula_gending"
                    else -> item.achievementKey.removePrefix("ach_")
                }

                val assetInfo = ArModelMapper.getAssetInfo(code)
                if (assetInfo != null) {
                    try {
                        val inputStream = holder.itemView.context.assets.open(assetInfo.markerPath)
                        val drawable = Drawable.createFromStream(inputStream, null)
                        holder.ivContent.setImageDrawable(drawable)
                        inputStream.close()
                    } catch (e: Exception) {
                        holder.ivContent.setImageResource(R.drawable.ic_ar)
                    }
                } else {
                    holder.ivContent.setImageResource(R.drawable.ic_ar)
                }

                val achievementId = when (item.achievementKey) {
                    "ach_tenun" -> "newbie"
                    "ach_gerabah" -> "ubah_nama"
                    "ach_gula" -> "ubah_tagline"
                    "ach_misoq" -> "ubah_foto"
                    "ach_menutu" -> "ubah_achievement"
                    "ach_begasingan" -> "penggunaan_ar"
                    "ach_gendang" -> "belajar_pertama"
                    "ach_nyongkolan" -> "buka_semua_huruf"
                    "ach_merariq" -> "buka_semua_wisata"
                    "ach_begawe" -> "buka_semua_budaya"
                    "ach_santekan" -> "selesai_stage_6"
                    "ach_nyale" -> "akurasi_menulis_90"
                    "ach_terjun" -> "akurasi_mengucapkan_90"
                    "ach_kuta" -> "naik_level_2"
                    "ach_sirkuit" -> "mencapai_level_5"
                    "ach_sade" -> "mencapai_level_10"
                    "ach_rinjani" -> "mencapai_level_15"
                    "ach_museum" -> "level_max"
                    "ach_bayan" -> "streak_5"
                    "ach_selaparang" -> "unlock_24_achievement"
                    else -> ""
                }

                val isAchieved = user?.unlockedBudaya?.contains(code) == true ||
                                 user?.unlockedWisata?.contains(code) == true ||
                                 (achievementId.isNotEmpty() && user?.unlockedAchievements?.contains(achievementId) == true)

                if (isAchieved) {
                    holder.ivContent.alpha = 1.0f
                    holder.ivLock.visibility = View.GONE
                    holder.card.setOnClickListener {
                        onActionTriggered(item, "AR_WISATA_BUDAYA")
                    }
                } else {
                    holder.ivContent.alpha = 0.4f
                    holder.ivLock.visibility = View.VISIBLE
                    holder.card.setOnClickListener {
                        Toast.makeText(
                            context,
                            "Dapatkan achievement '${item.name}' terlebih dahulu!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = listItems.size

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHeaderTitle: TextView = view.findViewById(R.id.tv_study_section_title)
    }

    class HurufViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: com.google.android.material.card.MaterialCardView =
            view.findViewById(R.id.card_huruf_grid)
        val ivAksara: ImageView = view.findViewById(R.id.iv_grid_aksara)
        val tvName: TextView = view.findViewById(R.id.tv_grid_name)
        val tvSub: TextView = view.findViewById(R.id.tv_grid_sub)
        val btnMenu: ImageButton = view.findViewById(R.id.btn_grid_menu)
        val ivLock: ImageView = view.findViewById(R.id.iv_grid_lock)
    }

    class WisataBudayaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: com.google.android.material.card.MaterialCardView =
            view.findViewById(R.id.card_wisata_budaya)
        val ivContent: ImageView = view.findViewById(R.id.iv_item_content)
        val tvTitle: TextView = view.findViewById(R.id.tv_item_title)
        val ivLock: ImageView = view.findViewById(R.id.iv_item_lock)
    }
}