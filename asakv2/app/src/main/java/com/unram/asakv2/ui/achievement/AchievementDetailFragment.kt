package com.unram.asakv2.ui.achievement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.unram.asakv2.R

/**
 * AchievementDetailFragment — Popup detail achievement.
 * Menampilkan informasi detail achievement dan opsi untuk menampilkannya di profil.
 * [DESTI]
 */
class AchievementDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_achievement_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Load detail achievement dari arguments/ViewModel
        // TODO: Setup tombol pilih tampil di profil
    }
}
