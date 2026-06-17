package com.unram.asakv2.ui.achievement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.unram.asakv2.R
import com.unram.asakv2.viewmodel.AchievementViewModel

/**
 * AchievementFragment — Grid achievement terkunci/terbuka.
 * Menampilkan daftar achievement dalam bentuk grid menggunakan RecyclerView.
 * [DESTI]
 */
class AchievementFragment : Fragment() {

    private lateinit var achievementViewModel: AchievementViewModel
    private lateinit var adapter: AchievementAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_achievement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        achievementViewModel = ViewModelProvider(this)[AchievementViewModel::class.java]

        // TODO: Setup RecyclerView dengan GridLayoutManager dan AchievementAdapter
        // TODO: Observe achievement list dari ViewModel
    }
}
