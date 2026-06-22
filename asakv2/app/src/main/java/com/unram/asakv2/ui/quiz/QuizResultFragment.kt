package com.unram.asakv2.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.unram.asakv2.R
import com.unram.asakv2.databinding.FragmentQuizResultBinding
import com.unram.asakv2.quiz.QuizScenario
import com.unram.asakv2.repository.UserRepository
import com.unram.asakv2.utils.ProgressManager

class QuizResultFragment : Fragment() {

    private var _binding: FragmentQuizResultBinding? = null
    private val binding get() = _binding!!
    private val userRepository = UserRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val totalExp     = arguments?.getInt("totalExp")     ?: 0
        val correctCount = arguments?.getInt("correctCount") ?: 0
        val totalCount   = arguments?.getInt("totalCount")   ?: 0
        val maxStreak    = arguments?.getInt("maxStreak")    ?: 0
        val stageId      = arguments?.getInt("stageId")      ?: 0
        val bagianId     = arguments?.getInt("bagianId")     ?: 0
        val isReplay     = arguments?.getBoolean("isReplay", false) ?: false

        val accuracy = if (totalCount > 0) correctCount.toFloat() / totalCount * 100 else 0f
        val isPassed = accuracy >= 75f

        binding.tvResultEmoji.text    = if (isPassed) "🎉" else "😢"
        binding.tvResultTitle.text    = if (isPassed) "Lulus!" else "Belum Lulus"
        binding.tvResultSubtitle.text = if (isPassed)
            "Kerja bagus! Terus semangat belajar."
        else
            "Jangan menyerah, coba lagi ya!"

        binding.tvAccuracy.text   = "Akurasi: ${accuracy.toInt()}% ($correctCount/$totalCount benar)"
        binding.tvTotalExp.text   = "Total EXP: +${if (isReplay) 0 else totalExp}"
        binding.tvMaxStreak.text  = "Streak Terbaik: ${if (isReplay) 0 else maxStreak}"
        binding.tvPassStatus.text = if (isPassed)
            "✅ Bagian ini selesai!"
        else
            "❌ Coba lagi untuk meningkatkan skor"

        // Banner akhir stage
        val totalBagian  = QuizScenario.allStages[stageId]?.size ?: 0
        val isLastBagian = stageId > 0 && bagianId == totalBagian
        binding.layoutStageComplete.visibility =
            if (isLastBagian && isPassed) View.VISIBLE else View.GONE
        if (isLastBagian && isPassed) {
            binding.tvStageCompleteEmoji.text = "🏅"
            binding.tvStageCompleteTitle.text = "Selamat!"
            binding.tvStageCompleteText.text  = "Kamu telah menyelesaikan Stage $stageId!"
        }

        var currentStageVal = ProgressManager.getCurrentStage(requireContext())
        var currentBagianVal = ProgressManager.getCurrentBagian(requireContext())

        // Simpan progress jika lulus dan bukan replay
        if (isPassed && stageId > 0 && !isReplay) {
            val nextProgress = ProgressManager.advanceProgress(requireContext(), stageId, bagianId)
            currentStageVal = nextProgress.first
            currentBagianVal = nextProgress.second
        }

        // Simpan hasil belajar, akurasi, streak dan trigger evaluasi achievement di Firestore (jika bukan replay)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null && !isReplay) {
            val isWriting = (bagianId == 3) // Sub-stage 3 is writing/CNN canvas
            val isSpeaking = (bagianId == 4) // Sub-stage 4 is speaking/voice recognition
            val completedStageId = if (isLastBagian && isPassed) stageId else 0

            userRepository.updateQuizResult(
                userId = currentUser.uid,
                xpGained = totalExp,
                accuracy = accuracy.toDouble(),
                isWriting = isWriting,
                isSpeaking = isSpeaking,
                streak = maxStreak,
                completedStageId = completedStageId,
                currentStage = currentStageVal,
                currentBagian = currentBagianVal
            ) { result ->
                // Progress updated successfully on Firestore
            }
        }

        binding.btnBackHome.setOnClickListener {
            findNavController().navigate(R.id.action_quizResult_to_home)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}