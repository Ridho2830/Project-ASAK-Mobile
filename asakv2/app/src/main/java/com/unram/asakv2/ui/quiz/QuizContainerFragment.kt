package com.unram.asakv2.ui.quiz

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.unram.asakv2.R
import com.unram.asakv2.achievement.AchievementUnlockManager
import com.unram.asakv2.databinding.FragmentQuizContainerBinding
import com.unram.asakv2.model.QuizPlan
import com.unram.asakv2.quiz.QuizScenario
import com.unram.asakv2.viewmodel.QuizViewModel

class QuizContainerFragment : Fragment() {

    private var _binding: FragmentQuizContainerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizViewModel by viewModels()

    private var stageId   = 1
    private var bagianId  = 1
    private var fromStudy = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AchievementUnlockManager.isInQuiz = true
        stageId   = arguments?.getInt("stageId")    ?: 1
        bagianId  = arguments?.getInt("bagianId")   ?: 1
        fromStudy = arguments?.getBoolean("fromStudy") ?: false

        val plans: List<QuizPlan> = if (fromStudy) {
            buildStudyPlans()
        } else {
            QuizScenario.allStages[stageId]?.get(bagianId) ?: emptyList()
        }

        viewModel.loadBagian(stageId, bagianId, plans)
        observeViewModel()

        binding.btnClose.setOnClickListener { showExitConfirmation() }
    }

    /**
     * Membuat daftar plan untuk mode belajar (dari halaman Belajar/StudyFragment).
     * Tipe 2 (Dengarkan) → expFull=0, isStreakMode=false
     * Tipe 3 (Menulis)   → expFull=0, isStreakMode=false
     */
    private fun buildStudyPlans(): List<QuizPlan> {
        val hurufId   = arguments?.getString("studyHuruf") ?: "ha"
        val studyMode = arguments?.getString("studyMode")  ?: "DENGAR"

        return when (studyMode) {
            "TULIS"    -> listOf(
                QuizPlan(tipe = 3, huruf = hurufId, expFull = 0, isStreakMode = false)
            )
            "DENGAR"   -> listOf(
                QuizPlan(tipe = 2, huruf = hurufId, expFull = 0, isStreakMode = false)
            )
            "AR_DUMMY" -> listOf(
                QuizPlan(tipe = -1, huruf = hurufId, expFull = 0, isStreakMode = false)
            )
            else -> emptyList()
        }
    }

    private fun observeViewModel() {
        viewModel.currentPlan.observe(viewLifecycleOwner) { plan ->
            plan?.let { loadQuizFragment(it) }
        }

        viewModel.isFinished.observe(viewLifecycleOwner) { finished ->
            if (finished) {
                if (fromStudy) {
                    // Mode belajar: tidak ada result screen, langsung kembali
                    findNavController().navigateUp()
                    return@observe
                }
                val result = viewModel.bagianResult.value ?: return@observe
                val isReplay = arguments?.getBoolean("isReplay", false) ?: false
                val bundle = Bundle().apply {
                    putInt("totalExp",     if (isReplay) 0 else result.totalExp)
                    putInt("correctCount", result.correctCount)
                    putInt("totalCount",   result.totalCount)
                    putInt("maxStreak",    result.maxStreak)
                    putInt("stageId",      stageId)
                    putInt("bagianId",     bagianId)
                    putBoolean("isReplay", isReplay)
                }
                findNavController().navigate(R.id.action_quizContainer_to_quizResult, bundle)
            }
        }
    }

    private fun loadQuizFragment(plan: QuizPlan) {
        val fragment: Fragment? = when (plan.tipe) {
            0 -> QuizTipe0Fragment.newInstance(plan.huruf, plan.expFull, plan.isStreakMode)
            1 -> QuizTipe1Fragment.newInstance(plan.huruf, plan.expFull, plan.isStreakMode)
            2 -> QuizTipe2Fragment.newInstance(plan.huruf, plan.expFull, plan.isStreakMode)
            3 -> QuizTipe3Fragment.newInstance(plan.huruf, plan.expFull, plan.isStreakMode)
            4 -> QuizTipe4Fragment.newInstance(plan.huruf, plan.expFull, plan.isStreakMode)
            5 -> {
                val pasangan = plan.pasangan ?: Pair("ha", "na")
                QuizTipe5Fragment.newInstance(
                    pasangan.first, pasangan.second, plan.expFull, plan.isStreakMode
                )
            }
            -1 -> QuizTipeArFragment.newInstance(stageId)
            else -> return
        }

        val idx   = viewModel.getCurrentIndex()
        val total = viewModel.getTotalPlans()

        if (!fromStudy) {
            binding.tvStatus.text = "${idx + 1} / $total"
        } else {
            binding.tvStatus.text = plan.huruf.uppercase()
        }

        if (fragment != null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.quizFragmentContainer, fragment)
                .commit()
        }
    }

    fun onQuizFragmentResult(expEarned: Int, isCorrect: Boolean) {
        viewModel.submitResult(expEarned, isCorrect)
    }

    fun getNextStreakIfCorrect(): Int = viewModel.getNextStreakIfCorrect()

    private fun showExitConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Keluar?")
            .setMessage("Yakin ingin keluar? Progress akan hilang.")
            .setPositiveButton("Keluar") { _, _ -> findNavController().navigateUp() }
            .setNegativeButton("Lanjutkan") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        AchievementUnlockManager.isInQuiz = false
        super.onDestroyView()
        _binding = null
    }
}