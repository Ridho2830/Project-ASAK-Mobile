package com.unram.asakv2.ui.quiz

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.unram.asakv2.R
import com.unram.asakv2.ai.HandwritingRecognizer
import com.unram.asakv2.databinding.FragmentQuizTipe3Binding
import com.unram.asakv2.utils.Constants
import com.unram.asakv2.utils.XpCalculator
import kotlinx.coroutines.launch

class QuizTipe3Fragment : Fragment() {

    private var _binding: FragmentQuizTipe3Binding? = null
    private val binding get() = _binding!!

    private lateinit var hurufId: String
    private var expFull = 20
    private var isStreakMode = true
    private var attemptCount = 0
    private var isWaitingForResult = false
    private val recognizer = HandwritingRecognizer()

    companion object {
        fun newInstance(hurufId: String, expFull: Int, isStreakMode: Boolean) =
            QuizTipe3Fragment().apply {
                arguments = Bundle().apply {
                    putString("hurufId", hurufId)
                    putInt("expFull", expFull)
                    putBoolean("isStreakMode", isStreakMode)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizTipe3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hurufId     = arguments?.getString("hurufId")      ?: "ha"
        expFull     = arguments?.getInt("expFull")         ?: 20
        isStreakMode = arguments?.getBoolean("isStreakMode") ?: true

        loadAksaraImage()
        setupUI()
    }

    private fun loadAksaraImage() {
        try {
            val stream = requireContext().assets.open("aksara/gambar/$hurufId.png")
            val drawable = android.graphics.drawable.Drawable.createFromStream(stream, null)
            binding.ivAksaraRef.setImageDrawable(drawable)
            binding.tvHurufLabel.text = Constants.HURUF_DISPLAY[hurufId] ?: hurufId.uppercase()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupUI() {
        binding.btnNext.isEnabled = false
        binding.btnNext.alpha = 0.5f

        if (isStreakMode) {
            
            binding.btnPensil.visibility     = View.GONE
            binding.btnHapus.visibility      = View.GONE
            binding.btnBersihkan.visibility  = View.GONE
        } else {
            
            binding.btnPensil.visibility     = View.VISIBLE
            binding.btnHapus.visibility      = View.VISIBLE
            binding.btnBersihkan.visibility  = View.VISIBLE

            updateToolButtonState(isPenActive = true)

            binding.btnPensil.setOnClickListener {
                binding.canvasView.isEraserMode = false
                updateToolButtonState(isPenActive = true)
            }

            binding.btnHapus.setOnClickListener {
                
                binding.canvasView.clearCanvas()
                binding.tvStatus.text = "Kanvas dibersihkan. Mulai lagi!"
            }

            binding.btnBersihkan.setOnClickListener {
                binding.canvasView.isEraserMode = true
                updateToolButtonState(isPenActive = false)
            }

            binding.btnNext.visibility = View.GONE
        }

        binding.canvasView.onStrokeEndListener = object : CanvasView.OnStrokeEndListener {
            override fun onStrokeEnd(bitmap: Bitmap) {
                if (!isWaitingForResult) {
                    if (isStreakMode && attemptCount >= 2) return
                    submitDrawing(bitmap)
                }
            }
        }
    }

    private fun updateToolButtonState(isPenActive: Boolean) {
        val activeColor   = Color.parseColor("#e74c3c")
        val inactiveColor = Color.parseColor("#9E9E9E")

        binding.btnPensil.backgroundTintList =
            android.content.res.ColorStateList.valueOf(
                if (isPenActive) activeColor else inactiveColor
            )
        binding.btnBersihkan.backgroundTintList =
            android.content.res.ColorStateList.valueOf(
                if (!isPenActive) activeColor else inactiveColor
            )
    }

    private fun submitDrawing(bitmap: Bitmap) {
        if (isWaitingForResult) return
        isWaitingForResult = true
        binding.tvStatus.text = "Menilai tulisan..."

        lifecycleScope.launch {
            val score = recognizer.recognize(bitmap, hurufId)
            isWaitingForResult = false
            handleScore(score)
        }
    }

    private fun handleScore(score: Float) {
        val isAccurate = recognizer.isAccurate(score)
        attemptCount++

        if (!isStreakMode) {
            
            binding.tvStatus.text = if (isAccurate)
                "Akurasi: ${score.toInt()}% ✅ Tepat!"
            else
                "Akurasi: ${score.toInt()}% ❌ Coba lagi"
            return
        }

        when {
            isAccurate -> {
                val expEarned = XpCalculator.calculateRetryExp(
                    expFull, isFirstAttempt = attemptCount == 1
                )
                showResult(true, score, expEarned)
            }
            attemptCount < 2 -> {
                binding.tvStatus.text = "Akurasi: ${score.toInt()}% — Coba sekali lagi!"
                binding.canvasView.clearCanvas()
            }
            else -> {
                showResult(false, score, 0)
            }
        }
    }

    private fun showResult(isCorrect: Boolean, score: Float, expEarned: Int) {
        val nextStreak = (parentFragment as? QuizContainerFragment)
            ?.getNextStreakIfCorrect() ?: 0
        val expPart = if (isCorrect)
            XpCalculator.buildFeedbackMessage(expEarned, nextStreak)
        else "0 EXP"

        QuizFeedbackDialog.show(
            context   = requireContext(),
            isSuccess = isCorrect,
            message   = "Akurasi: ${score.toInt()}%\n$expPart"
        ) {
            binding.btnNext.isEnabled = true
            binding.btnNext.alpha     = 1.0f
            notifyParent(expEarned, isCorrect)
        }
    }

    private fun notifyParent(expEarned: Int, isCorrect: Boolean) {
        (parentFragment as? QuizContainerFragment)
            ?.onQuizFragmentResult(expEarned, isCorrect)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}