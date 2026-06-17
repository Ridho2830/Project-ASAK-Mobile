package com.unram.asakv2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.unram.asakv2.R
import com.unram.asakv2.viewmodel.HomeViewModel

/**
 * HomeFragment — Beranda: XP bar, streak, level progress.
 * Menampilkan progress belajar aksara pengguna.
 * [KANDA]
 */
class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // TODO: Observe XP, streak, level progress dari ViewModel
    }
}
