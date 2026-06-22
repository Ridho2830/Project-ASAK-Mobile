package com.unram.asakv2.ui.quiz

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.unram.asakv2.R
import com.unram.asakv2.databinding.FragmentQuizTipe2Binding
import com.unram.asakv2.utils.Constants
import com.unram.asakv2.utils.XpCalculator

class QuizTipe2Fragment : Fragment() {

    private var _binding: FragmentQuizTipe2Binding? = null
    private val binding get() = _binding!!

    private lateinit var hurufId: String
    private var expFull = 15
    private var isStreakMode = true
    private var playCount = 0
    private var answered = false
    private var mediaPlayer: MediaPlayer? = null
    private var waveformAnimators: List<android.animation.ObjectAnimator> = emptyList()

    companion object {
        fun newInstance(hurufId: String, expFull: Int, isStreakMode: Boolean): QuizTipe2Fragment {
            return QuizTipe2Fragment().apply {
                arguments = Bundle().apply {
                    putString("hurufId", hurufId)
                    putInt("expFull", expFull)
                    putBoolean("isStreakMode", isStreakMode)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQuizTipe2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hurufId = arguments?.getString("hurufId") ?: "ha"
        expFull = arguments?.getInt("expFull") ?: 15
        isStreakMode = arguments?.getBoolean("isStreakMode") ?: true

        setupQuestion()
    }

    private fun setupQuestion() {
        val options = generateOptions(hurufId)
        val optionButtons = listOf(
            binding.btnOption1, binding.btnOption2,
            binding.btnOption3, binding.btnOption4
        )

        optionButtons.forEachIndexed { i, btn ->
            btn.text = Constants.HURUF_DISPLAY[options[i]] ?: options[i].uppercase()
            btn.setBackgroundColor(requireContext().getColor(R.color.option_default))
            btn.isEnabled = true
            btn.setOnClickListener {
                if (!answered) handleAnswer(options[i], btn, optionButtons)
            }
        }

        binding.btnPlayAudio
            .setOnClickListener {
                playAudio()
            }

        binding.btnNext.isEnabled = false
        binding.btnNext.alpha = 0.5f
    }

    private fun playAudio() {
        if (answered) return
        playCount++
        binding.tvPlayCount.text = "Diputar: ${playCount}x"

        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer()
            val afd: AssetFileDescriptor = requireContext().assets.openFd("aksara/audio/$hurufId.mp3")
            mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mediaPlayer?.setOnCompletionListener { stopWaveformAnimation() }
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            startWaveformAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startWaveformAnimation() {
        stopWaveformAnimation()
        val bars = listOf(
            binding.bar1,  binding.bar2,  binding.bar3,  binding.bar4,  binding.bar5,
            binding.bar6,  binding.bar7,  binding.bar8,  binding.bar9,  binding.bar10,
            binding.bar11, binding.bar12, binding.bar13, binding.bar14, binding.bar15,
            binding.bar16, binding.bar17, binding.bar18, binding.bar19, binding.bar20,
            binding.bar21, binding.bar22, binding.bar23, binding.bar24, binding.bar25,
            binding.bar26, binding.bar27, binding.bar28, binding.bar29, binding.bar30,
            binding.bar31, binding.bar32, binding.bar33, binding.bar34, binding.bar35,
            binding.bar36, binding.bar37, binding.bar38, binding.bar39, binding.bar40,
            binding.bar41, binding.bar42, binding.bar43, binding.bar44, binding.bar45,
            binding.bar46, binding.bar47, binding.bar48, binding.bar49, binding.bar50,
            binding.bar51, binding.bar52, binding.bar53, binding.bar54, binding.bar55,
            binding.bar56, binding.bar57, binding.bar58, binding.bar59, binding.bar60
        )
        waveformAnimators = bars.mapIndexed { i, bar ->
            val anim = android.animation.ObjectAnimator.ofFloat(bar, "scaleY", 0.3f, 1.5f, 0.5f)
            anim.duration = 350L + (i % 5) * 70L
            anim.repeatCount = android.animation.ObjectAnimator.INFINITE
            anim.repeatMode = android.animation.ObjectAnimator.REVERSE
            anim.startDelay = (i * 25L)
            anim.start()
            anim
        }
    }

    private fun stopWaveformAnimation() {
        waveformAnimators.forEach { it.cancel() }
        waveformAnimators = emptyList()
        listOf(
            binding.bar1,  binding.bar2,  binding.bar3,  binding.bar4,  binding.bar5,
            binding.bar6,  binding.bar7,  binding.bar8,  binding.bar9,  binding.bar10,
            binding.bar11, binding.bar12, binding.bar13, binding.bar14, binding.bar15,
            binding.bar16, binding.bar17, binding.bar18, binding.bar19, binding.bar20,
            binding.bar21, binding.bar22, binding.bar23, binding.bar24, binding.bar25,
            binding.bar26, binding.bar27, binding.bar28, binding.bar29, binding.bar30,
            binding.bar31, binding.bar32, binding.bar33, binding.bar34, binding.bar35,
            binding.bar36, binding.bar37, binding.bar38, binding.bar39, binding.bar40,
            binding.bar41, binding.bar42, binding.bar43, binding.bar44, binding.bar45,
            binding.bar46, binding.bar47, binding.bar48, binding.bar49, binding.bar50,
            binding.bar51, binding.bar52, binding.bar53, binding.bar54, binding.bar55,
            binding.bar56, binding.bar57, binding.bar58, binding.bar59, binding.bar60
        ).forEach { it.scaleY = 1f }
    }

    private fun generateOptions(correct: String): List<String> {
        val others = Constants.HURUF_LIST.filter { it != correct }.shuffled().take(3)
        return (others + correct).shuffled()
    }

    private fun handleAnswer(chosen: String, clickedBtn: Button, allBtns: List<Button>) {
        answered = true
        val isCorrect = chosen == hurufId

        if (isCorrect) {
            clickedBtn.setBackgroundColor(Color.parseColor("#2ecc71"))
        } else {
            clickedBtn.setBackgroundColor(Color.parseColor("#e74c3c"))
            allBtns.forEach { btn ->
                if (btn.text.toString().equals(
                        Constants.HURUF_DISPLAY[hurufId] ?: hurufId, ignoreCase = true)) {
                    btn.setBackgroundColor(Color.parseColor("#2ecc71"))
                }
            }
        }

        val expEarned = if (isCorrect) {
            if (isStreakMode) XpCalculator.calculateTipe2Exp(expFull, playCount)
            else expFull
        } else 0
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
        stopWaveformAnimation()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }
}