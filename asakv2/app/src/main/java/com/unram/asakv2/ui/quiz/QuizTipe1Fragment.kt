package com.unram.asakv2.ui.quiz

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.unram.asakv2.R
import com.unram.asakv2.databinding.FragmentQuizTipe1Binding
import com.unram.asakv2.utils.Constants
import com.unram.asakv2.utils.XpCalculator

class QuizTipe1Fragment : Fragment() {

    private var _binding: FragmentQuizTipe1Binding? = null
    private val binding get() = _binding!!

    private lateinit var hurufId: String
    private var expFull = 10
    private var isStreakMode = true
    private var answered = false

    companion object {
        fun newInstance(hurufId: String, expFull: Int, isStreakMode: Boolean): QuizTipe1Fragment {
            return QuizTipe1Fragment().apply {
                arguments = Bundle().apply {
                    putString("hurufId", hurufId)
                    putInt("expFull", expFull)
                    putBoolean("isStreakMode", isStreakMode)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizTipe1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hurufId = arguments?.getString("hurufId") ?: "ha"
        expFull = arguments?.getInt("expFull") ?: 10
        isStreakMode = arguments?.getBoolean("isStreakMode") ?: true

        setupQuestion()
    }

    private fun setupQuestion() {
        // Load image aksara from assets
        try {
            val assetManager = requireContext().assets
            val inputStream = assetManager.open("aksara/gambar/$hurufId.png")
            val drawable = android.graphics.drawable.Drawable.createFromStream(inputStream, null)
            binding.ivAksara.setImageDrawable(drawable)
        } catch (e: Exception) {
            binding.ivAksara.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // Generate 4 options: 1 correct + 3 random distractors
        val options = generateOptions(hurufId)

        val optionButtons = listOf(
            binding.btnOption1,
            binding.btnOption2,
            binding.btnOption3,
            binding.btnOption4
        )

        optionButtons.forEachIndexed { index, btn ->
            btn.text = Constants.HURUF_DISPLAY[options[index]] ?: options[index].uppercase()
            btn.setBackgroundColor(requireContext().getColor(R.color.option_default))
            btn.isEnabled = true
            btn.setOnClickListener {
                if (!answered) handleAnswer(options[index], btn, optionButtons)
            }
        }

        binding.btnNext.isEnabled = false
        binding.btnNext.alpha = 0.5f
        binding.btnNext.setOnClickListener {
            // Not handled here — parent container handles navigation
        }
    }

    private fun generateOptions(correct: String): List<String> {
        val others = Constants.HURUF_LIST.filter { it != correct }.shuffled().take(3)
        return (others + correct).shuffled()
    }

    private fun handleAnswer(chosen: String, clickedBtn: Button, allBtns: List<Button>) {
        answered = true
        val isCorrect = chosen == hurufId

        // Visual feedback
        if (isCorrect) {
            clickedBtn.setBackgroundColor(Color.parseColor("#2ecc71"))
        } else {
            clickedBtn.setBackgroundColor(Color.parseColor("#e74c3c"))
            // Show correct answer in green
            allBtns.forEach { btn ->
                if (btn.text.toString().lowercase() ==
                    (Constants.HURUF_DISPLAY[hurufId] ?: hurufId).lowercase()) {
                    btn.setBackgroundColor(Color.parseColor("#2ecc71"))
                }
            }
        }

        val expEarned = if (isCorrect) expFull else 0
        val nextStreak = (parentFragment as? QuizContainerFragment)?.getNextStreakIfCorrect() ?: 0

        val message = if (isCorrect) {
            XpCalculator.buildFeedbackMessage(expEarned, nextStreak)
        } else {
            "0 EXP"
        }

        QuizFeedbackDialog.show(
            context = requireContext(),
            isSuccess = isCorrect,
            message = message
        ) {
            binding.btnNext.isEnabled = true
            binding.btnNext.alpha = 1.0f
            notifyParent(expEarned, isCorrect)
        }
    }

    private fun notifyParent(expEarned: Int, isCorrect: Boolean) {
        (parentFragment as? QuizContainerFragment)?.onQuizFragmentResult(expEarned, isCorrect)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}