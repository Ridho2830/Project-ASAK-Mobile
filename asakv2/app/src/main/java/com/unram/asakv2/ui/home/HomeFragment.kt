package com.unram.asakv2.ui.home

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.unram.asakv2.R
import com.unram.asakv2.model.SubStageProgress
import com.unram.asakv2.utils.ProgressManager
import com.unram.asakv2.utils.XpCalculator
import com.unram.asakv2.viewmodel.ProfileViewModel

class HomeFragment : Fragment() {

    private lateinit var tvLevelStatus: TextView
    private lateinit var pbXp: ProgressBar
    private lateinit var subStageList: List<SubStageProgress>
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvLevelStatus = view.findViewById(R.id.tv_level_status)
        pbXp = view.findViewById(R.id.pb_xp)

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                val currentLevelXp = XpCalculator.xpRequiredForLevel(user.level)
                val nextLevelXp = XpCalculator.xpRequiredForLevel(user.level + 1)
                val xpInCurrentLevel = (user.xp - currentLevelXp).coerceAtLeast(0)
                val xpNeeded = (nextLevelXp - currentLevelXp).coerceAtLeast(1)

                pbXp.max = xpNeeded
                pbXp.progress = xpInCurrentLevel
                tvLevelStatus.text = "Lv. ${user.level} ($xpInCurrentLevel / $xpNeeded)"

                // Re-render progression map dynamically after synchronization
                if (isAdded) {
                    renderGameProgression(requireView(), user.currentStage, user.currentBagian)
                }
            }
        }

        initSubStageStructure()
        refreshUI(view)
    }

    override fun onResume() {
        super.onResume()
        // Refresh setiap kali kembali ke home (setelah quiz selesai)
        view?.let { refreshUI(it) }
    }

    private fun refreshUI(view: View) {
        val currentStage  = ProgressManager.getCurrentStage(requireContext())
        val currentBagian = ProgressManager.getCurrentBagian(requireContext())

        // Load profile to trigger observer update for tvLevelStatus and pbXp
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            profileViewModel.loadProfile(currentUser.uid)
        }

        renderGameProgression(view, currentStage, currentBagian)
    }

    private fun calculateTotalCompleted(currentStage: Int, currentBagian: Int): Int {
        // Hitung berapa total bagian yang sudah selesai berdasarkan posisi aktif
        val bagianPerStage = mapOf(1 to 5, 2 to 5, 3 to 6, 4 to 6, 5 to 6, 6 to 3)
        var total = 0
        for (s in 1 until currentStage) {
            total += bagianPerStage[s] ?: 0
        }
        total += currentBagian - 1
        return total
    }

    private fun initSubStageStructure() {
        subStageList = listOf(
            // === STAGE 1 ===
            SubStageProgress(R.id.btn_s1_sub1, 1, 1, "BUKU",   R.color.stage1_start),
            SubStageProgress(R.id.btn_s1_sub2, 1, 2, "BUKU",   R.color.stage1_start),
            SubStageProgress(R.id.btn_s1_sub3, 1, 3, "BUKU",   R.color.stage1_start),
            SubStageProgress(R.id.btn_s1_sub4, 1, 4, "KAMERA", R.color.stage1_start),
            SubStageProgress(R.id.btn_s1_sub5, 1, 5, "PUZZLE", R.color.stage1_start),

            // === STAGE 2 ===
            SubStageProgress(R.id.btn_s2_sub1, 2, 1, "BUKU",   R.color.stage2_start),
            SubStageProgress(R.id.btn_s2_sub2, 2, 2, "BUKU",   R.color.stage2_start),
            SubStageProgress(R.id.btn_s2_sub3, 2, 3, "BUKU",   R.color.stage2_start),
            SubStageProgress(R.id.btn_s2_sub4, 2, 4, "KAMERA", R.color.stage2_start),
            SubStageProgress(R.id.btn_s2_sub5, 2, 5, "PUZZLE", R.color.stage2_start),

            // === STAGE 3 ===
            SubStageProgress(R.id.btn_s3_sub1, 3, 1, "BUKU",   R.color.stage3_start),
            SubStageProgress(R.id.btn_s3_sub2, 3, 2, "BUKU",   R.color.stage3_start),
            SubStageProgress(R.id.btn_s3_sub3, 3, 3, "BUKU",   R.color.stage3_start),
            SubStageProgress(R.id.btn_s3_sub4, 3, 4, "KAMERA", R.color.stage3_start),
            SubStageProgress(R.id.btn_s3_sub5, 3, 5, "PUZZLE", R.color.stage3_start),
            SubStageProgress(R.id.btn_s3_sub6, 3, 6, "PUZZLE", R.color.stage3_start),

            // === STAGE 4 ===
            SubStageProgress(R.id.btn_s4_sub1, 4, 1, "BUKU",   R.color.stage4_start),
            SubStageProgress(R.id.btn_s4_sub2, 4, 2, "BUKU",   R.color.stage4_start),
            SubStageProgress(R.id.btn_s4_sub3, 4, 3, "BUKU",   R.color.stage4_start),
            SubStageProgress(R.id.btn_s4_sub4, 4, 4, "KAMERA", R.color.stage4_start),
            SubStageProgress(R.id.btn_s4_sub5, 4, 5, "PUZZLE", R.color.stage4_start),
            SubStageProgress(R.id.btn_s4_sub6, 4, 6, "PUZZLE", R.color.stage4_start),

            // === STAGE 5 ===
            SubStageProgress(R.id.btn_s5_sub1, 5, 1, "BUKU",   R.color.stage5_start),
            SubStageProgress(R.id.btn_s5_sub2, 5, 2, "BUKU",   R.color.stage5_start),
            SubStageProgress(R.id.btn_s5_sub3, 5, 3, "BUKU",   R.color.stage5_start),
            SubStageProgress(R.id.btn_s5_sub4, 5, 4, "KAMERA", R.color.stage5_start),
            SubStageProgress(R.id.btn_s5_sub5, 5, 5, "PUZZLE", R.color.stage5_start),
            SubStageProgress(R.id.btn_s5_sub6, 5, 6, "PUZZLE", R.color.stage5_start),

            // === STAGE 6 ===
            SubStageProgress(R.id.btn_s6_sub1, 6, 1, "PUZZLE", R.color.stage6_start),
            SubStageProgress(R.id.btn_s6_sub2, 6, 2, "PUZZLE", R.color.stage6_start),
            SubStageProgress(R.id.btn_s6_sub3, 6, 3, "PUZZLE", R.color.stage6_start)
        )
    }

    private fun renderGameProgression(rootView: View, currentStage: Int, currentBagian: Int) {
        for (item in subStageList) {
            val fab = rootView.findViewById<FloatingActionButton>(item.buttonId) ?: continue

            val isCompleted = item.stageNumber < currentStage ||
                    (item.stageNumber == currentStage && item.subStageNumber < currentBagian)

            val isActive = item.stageNumber == currentStage && item.subStageNumber == currentBagian

            when {
                isCompleted -> {
                    fab.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), item.stageColorRes)
                    )
                    fab.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.white)
                    )
                    fab.setOnClickListener {
                        navigateToQuiz(item.stageNumber, item.subStageNumber, isReplay = true)
                    }
                }

                isActive -> {
                    fab.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.gold_accent)
                    )
                    fab.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.white)
                    )
                    fab.setOnClickListener {
                        navigateToQuiz(item.stageNumber, item.subStageNumber, isReplay = false)
                    }
                }

                else -> {
                    fab.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.gray_light)
                    )
                    fab.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.gray_dark)
                    )
                    fab.setOnClickListener {
                        Toast.makeText(requireContext(),
                            "Selesaikan tantangan saat ini terlebih dahulu!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun navigateToQuiz(stageId: Int, bagianId: Int, isReplay: Boolean) {
        val bundle = Bundle().apply {
            putInt("stageId", stageId)
            putInt("bagianId", bagianId)
            putBoolean("fromStudy", false)
            putBoolean("isReplay", isReplay)
        }
        findNavController().navigate(R.id.action_home_to_quizContainer, bundle)
    }
}